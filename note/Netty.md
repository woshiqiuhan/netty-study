Netty

`writeAndFlush` 详解

> 此处仅基于初步的认识

在对客户端、服务端之间进行通信时，即消息的发送与接收，会使用到 `writeAndFlush`

有几点注意：

1. `ChannelHandlerContext ctx` 可以调用 `writeAndFlush` 函数，`ctx.channel()` 也可以，有什么区别？

   首先看一下案例

   服务端启动程序

   ```java
   public class Server {
       public static void main(String[] args) throws InterruptedException {
           EventLoopGroup bossGroup = new NioEventLoopGroup();
           EventLoopGroup workerGroup = new NioEventLoopGroup();
   
           try {
               ChannelFuture channelFuture = new ServerBootstrap()
                       .group(bossGroup, workerGroup)
                       .channel(NioServerSocketChannel.class)
                       .childHandler(new ChannelInitializer<SocketChannel>() {
                           @Override
                           protected void initChannel(SocketChannel ch) throws Exception {
                               ChannelPipeline pipeline = ch.pipeline();
                               pipeline.addLast("decoder", new StringDecoder());
                               pipeline.addLast("encoder", new StringEncoder());
   
                               // OutboundHandler 的执行顺序为 C -> B -> A
                               pipeline.addLast(new OutboundHandlerA());
                               pipeline.addLast(new OutboundHandlerB());
                               pipeline.addLast(new OutboundHandlerC());
                           }
                       })
                       .option(ChannelOption.SO_BACKLOG, 128)
                       .childOption(ChannelOption.SO_KEEPALIVE, true)
                       .bind(7731).sync();
               System.out.println("服务端启动");
               channelFuture.channel().closeFuture().sync();
           } finally {
               workerGroup.shutdownGracefully();
               bossGroup.shutdownGracefully();
           }
       }
   }
   ```

   `OutboundHandler` 处理函数

   ```java
   public class OutboundHandlerA extends ChannelOutboundHandlerAdapter {
       @Override
       public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
           System.out.println("OutboundHandlerA.write");
           ctx.write(msg, promise);
       }
   }
   
   public class OutboundHandlerB extends ChannelOutboundHandlerAdapter {
       @Override
       public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
           System.out.println("OutboundHandlerB.write");
           ctx.write(msg, promise);
       }
   
       @Override
       public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
           ctx.executor().schedule(() -> {
               // ctx.writeAndFlush("say hello");
               ctx.channel().writeAndFlush("say hello");
           }, 3, TimeUnit.SECONDS);
       }
   }
   
   public class OutboundHandlerC extends ChannelOutboundHandlerAdapter {
       @Override
       public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
           System.out.println("OutboundHandlerC.write");
           ctx.write(msg, promise);
       }
   }
   ```

   写入操作于 `OutboundHandlerB.handlerAdded` 方法中触发

   如果调用 `ctx.channel().writeAndFlush("say hello");` 执行结果如图所示

   ```tex
   服务端启动
   OutboundHandlerC.write
   OutboundHandlerB.write
   OutboundHandlerA.write
   ```

   而调用 `ctx.writeAndFlush("say hello");` 执行结果为

   ```tex
   服务端启动
   OutboundHandlerA.write
   ```

   不难得出结论：

   - 调用 `ctx.writeAndFlush` 方法，是将数据由最后一个 `OutboundHandler` 直接传出，而不经由 `OutboundHandler` 链
   - 调用 `ctx.channel().writeAndFlush` 方法则是，交由头上的 `OutboundHandler` 然后经由链从尾部 `OutboundHandler` 传出

2. 只有在加入相对应的编解码器时，才可以直接进行相应对象的传输，否则传输会失败

   如传输字符串

   - 在加入

     ```java
     pipeline.addLast("decoder", new StringDecoder());
     pipeline.addLast("encoder", new StringEncoder());
     ```

     的编解码器后，可直接调用 `writeAndFlush("Hello")` 写入

   - 而未加入时，直接写入会失败，需使用 `Unpooled.copiedBuffer` 方法进行操作，即使用 `ByteBuf` 缓冲区进行**转换写入**

     ```java
     ctx.writeAndFlush(Unpooled.copiedBuffer("Hello", CharsetUtil.UTF_8));
     ```

   

**Netty 中常用的编解码器**

编解码

- 编码(Encode)：序列化(Serialization)，即**将对象序列化成字节数组**，用于网络传输、数据持久化或其他用途
- 解码(Decode)：反序列化(Deserialization)，**把从网络、磁盘等读取到的字节数组还原成原始对象**(通常是原始对象的**拷贝**)

Netty 提供了编解码器的实现，具体百度

**Netty 本身提供的编解码器的机制和问题分析**

- `StringEncoder`/`StringDecoder`，字符串编解码

- `ObjectEncoder`/`ObjectDecoder`，Java 对象的编解码

  存在的问题

  - 对于 Java 对象的编解码底层仍然使用的是 Java 序列化机制，效率不高
  - 无法跨语言
  - 序列化后体积太大

基于以上问题，引入解决方法 Google ProtoBuf

**ProtoBuf 的使用**

Google 的 Protobuf 作为一门开源的高性能的编解码框架，在通信框架中扮演者很重要的角色，很多商业项目将其作为编解码框架Protobuf有以下优点：

- 产品非常成熟
- 跨语言，不局限java
- 编码后消息很小，利于存储和传输
- 编码性能高
- 支持不同版本的协议前后兼容
- 支持定义可选和必选字段

**环境搭建**

> 基于 Mac OS X aarch64

```bash
# 使用brew安装protobuf
brew install protobuf

# 检查 protobuf 是否安装成功
protoc --version

# 使用 protoc 将 .proto 文件转成 .java 文件
protoc --java_out=. Student.proto
```

**使用的具体流程如下**

1. 编写需传输对象相应的 `.proto` 文件，[语法参见](https://developers.google.com/protocol-buffers/docs/downloads)
2. 使用 `protoc` 工具将 `.proto` 文件转为 `.java` 文件
3. 在客户端、服务端 `pipeline` 加入相应的 `ProtobufDecoder` 和 `ProtobufEncoder`编解码器
4. 数据传输

**案例：`Student.proto`**

```protobuf
// 声明协议的版本
syntax = "proto3";

// 设置生成的外部类名，同时也是文件名
option java_outer_classname = "StudentOuter";

// protobuf 使用 message 管理数据，类似 class
message Student {
  // 会在 StudentOuter 中生成一个内部类，是真正发送的对象
  // 声明属性，1不是值 而是代表属性序号
  int32 id = 1;
  string name = 2;
}
```

```java
// 加入相应的编解码器

// 加入对应编码器
pipeline.addLast("encoder", new ProtobufEncoder());

// 加入相应解码器
pipeline.addLast("decoder", new ProtobufDecoder(
            StudentOuter.Student.getDefaultInstance()));

// 数据发送
StudentOuter.Student student =StudentOuter.Student.newBuilder().setId(1).setName("秋寒").build();
ctx.writeAndFlush(student);

// 数据接收
StudentOuter.Student student = (StudentOuter.Student) msg;
System.out.println(student.getId() + " " + student.getName());
```

**Log4J 整合到 Netty**

