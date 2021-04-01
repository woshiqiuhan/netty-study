package cn.hznu.taskqueue;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    public static void main(String[] args) {
        // 客户端仅需一个事件循环组
        EventLoopGroup group = new NioEventLoopGroup();

        // 客户端启动对象
        Bootstrap bootstrap = new Bootstrap();
        // 设置相关参数
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientChannelInitializer());

        try {
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8868).sync();
            System.out.println("客户端启动");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println("client start fail");
        } finally {
            group.shutdownGracefully();
        }
    }
}
