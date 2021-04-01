package cn.hznu.protostuff.consumer.proxy;

import cn.hznu.rpc.codec.RpcEncoder;
import cn.hznu.rpc.protocol.InvokerProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcProxy {
    @SuppressWarnings("all")
    public static <T> T create(Class<?> aClass) {
        MethodProxy methodProxy = new MethodProxy(aClass);
        // 得到传入类实现的接口
        Class<?>[] interfaces = aClass.isInterface() ?
                new Class<?>[]{aClass} :
                aClass.getInterfaces();

        // 获取动态代理对象
        return (T) Proxy.newProxyInstance(
                aClass.getClassLoader(), interfaces, methodProxy);
    }

    private static class MethodProxy implements InvocationHandler {

        private final Class<?> aClass;

        public MethodProxy(Class<?> aClass) {
            this.aClass = aClass;
        }

        // 动态代理执行方法
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 判断传进来的是否是一个已实现的具体类
            if (Object.class.equals(method.getDeclaringClass())) {
                try {
                    // 如果是，直接执行并返回即可
                    return method.invoke(this, args);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    return null;
                }
            } else {
                // 否则进行远程调用
                return rpcInvoke(proxy, method, args);
            }
        }

        // 实现接口的核心方法
        public Object rpcInvoke(Object proxy, Method method, Object[] args) throws InterruptedException {
            // 创建要传输的对象
            InvokerProtocol invokerProtocol = InvokerProtocol.builder()
                    .className(aClass.getName())
                    .methodName(method.getName())
                    .params(method.getParameterTypes())
                    .values(args).build();

            EventLoopGroup workerGroup = new NioEventLoopGroup();

            // 自定义接收 handler
            final RpcProxyHandler consumerHandler = new RpcProxyHandler();
            try {
                ChannelFuture channelFuture = new Bootstrap()
                        .group(workerGroup)
                        .channel(NioSocketChannel.class)
                        // 设置低延迟
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();

                                // 编解码器
                                // pipeline.addLast("decode", new RpcDecoder());
                                pipeline.addLast("decoder", new ObjectDecoder(
                                        Integer.MAX_VALUE,
                                        ClassResolvers.cacheDisabled(null)));
                                pipeline.addLast("encode", new RpcEncoder());

                                pipeline.addLast("handler", consumerHandler);
                            }
                        })
                        .connect("localhost", 8080).sync();
                // 发送远程调用请求
                channelFuture.channel().writeAndFlush(invokerProtocol).sync();
                channelFuture.channel().closeFuture().sync();
            } finally {
                workerGroup.shutdownGracefully();
            }
            return consumerHandler.getResponse();
        }
    }
}