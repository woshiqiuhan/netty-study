package cn.hznu.chatroom.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

public class Client {
    private final int port;
    private final String host;
    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private Channel channel;

    public Client(String host, int port) {
        this.port = port;
        this.host = host;

        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.bootstrap
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ClientChannelInitializer());
    }

    public void connect() throws InterruptedException {
        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            this.channel = channelFuture.channel();
            System.out.println("客户端启动成功，连接服务器 " + this.channel.remoteAddress());

            // 开始监听客户端写入
            write();

            channelFuture.channel().closeFuture().sync();
        } finally {
            this.group.shutdownGracefully();
        }
    }

    private void write() {
        Scanner in = new Scanner(System.in);
        while (in.hasNext()) {
            this.channel.writeAndFlush(in.nextLine());
        }
    }
}
