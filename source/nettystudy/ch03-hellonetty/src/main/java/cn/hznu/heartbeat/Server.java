package cn.hznu.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class Server {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 添加日志显示
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                        /*
                            加入 netty 提供的处理空闲状态的处理器 IdleStateHandler
                            即一种触发器，当多久没进行读/写操作后，判定为读/写空闲，发送心跳检测包检测是否仍然连接
                                readerIdleTime：表示多久没有进行读操作后发送心跳检测包测试是否连接
                                writerIdleTime：表示多久没有进行写操作后发送心跳检测包测试是否连接
                                allIdleTime：表示多久没有进行读、写操作后发送心跳检测包测试是否连接
                         */
                            pipeline.addLast(new IdleStateHandler(
                                    3, 5,
                                    7, TimeUnit.SECONDS));
                            // 当 IdleStateHandler 触发后，
                            // 就会传递给管道的下一个 handler 中的 userEventTriggered 进行相应处理
                            pipeline.addLast(new ServerHandler());
                        }
                    }).bind(8863).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
