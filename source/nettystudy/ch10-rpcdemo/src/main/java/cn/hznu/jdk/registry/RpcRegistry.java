package cn.hznu.jdk.registry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * registry 注册中心对作用是负责将所有 provider
 * 的服务名和服务引用地址注册到一个容器中，对外发布
 */
public class RpcRegistry {
    private final int port;

    public RpcRegistry(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 解码器
                            /*
                                LengthFieldBasedFrameDecoder
                                    maxFrameLength：数据帧的最大长度，若超出抛出 TooLongFrameException 异常
                                    lengthFieldOffset：长度属性的偏移量，用于确定长度属性在数据中的位置
                                    lengthFieldLength：长度字段的大小，长度对应 int(4), long(8)
                                    lengthAdjustment：要添加到长度属性的补偿值
                                    initialBytesToStrip：从解码帧中去除的第一个字节数
                             */
                            pipeline.addLast("frameDecoder",
                                    new LengthFieldBasedFrameDecoder(
                                    Integer.MAX_VALUE,
                                    0,
                                    4,
                                    0,
                                    4));

                            // 编码器
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));

                            pipeline.addLast("encoder", new ObjectEncoder());
                            pipeline.addLast("decoder", new ObjectDecoder(
                                    Integer.MAX_VALUE,
                                    ClassResolvers.cacheDisabled(null)));

                            // 自定义注册中心 handler
                            pipeline.addLast("handler", new RegistryHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new RpcRegistry(8080).start();
    }
}
