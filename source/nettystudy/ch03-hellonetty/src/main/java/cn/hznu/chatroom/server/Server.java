package cn.hznu.chatroom.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Scanner;

public class Server {
    private final int port;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final ServerBootstrap serverBootstrap;
    private Channel channel;

    public Server(int port) {
        this.port = port;
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();

        this.serverBootstrap = new ServerBootstrap();
        this.serverBootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ServerChannelInitializer());
    }

    public void run() throws InterruptedException {
        try {
            // 启动服务端
            ChannelFuture channelFuture = this.serverBootstrap.bind(this.port).sync();
            this.channel = channelFuture.channel();
            System.out.println("服务器 " + this.channel.localAddress() + " 启动完成");

            // 监听写入
            write();

            // 使得当前线程 wait
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 资源释放
            this.bossGroup.shutdownGracefully();
            this.workerGroup.shutdownGracefully();
        }
    }

    private void write() {
        Scanner in = new Scanner(System.in);
        while (in.hasNext()) {
            String str = "[广播] " + in.nextLine();
            System.out.println(str);
            ServerHandler.channels.writeAndFlush(str);
        }
    }
}
