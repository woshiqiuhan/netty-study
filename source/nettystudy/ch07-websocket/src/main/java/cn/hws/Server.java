package cn.hws;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class Server {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 添加日志显示
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            // 使用 HTTP 协议的 编码器和解码器
                            pipeline.addLast(new HttpServerCodec());
                            // 数据以块状形式读写，添加对应处理器
                            pipeline.addLast(new ChunkedWriteHandler());

                            /*
                                说明：
                                    由于 HTTP 数据在传输过程中是分段的，
                                    HttpObjectAggregator 其作用是将多段数据聚合
                                    这就是为什么浏览器在发送大量数据时，会发出多次 HTTP 请求
                             */
                            pipeline.addLast(new HttpObjectAggregator(8192));

                            /*
                                说明：
                                    对应 WebSocket 其数据是以 frame 的格式传递
                                    浏览器请求的 ws://localhost:8863/hello 表示请求的 uri
                        WebSocketServerProtocolHandler 的核心功能是将 HTTP 协议升级为 ws 协议，保持长连接
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));

                            // 自定义 handler 处理业务
                            pipeline.addLast(new MyServerHandler());
                        }
                    }).bind(8863).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
