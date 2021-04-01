package cn.hznu.taskqueue;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
    public static void main(String[] args) {
        /**
         * 创建 BossGroup 和 WorkerGroup
         *
         * bossGroup 和 workerGroup 是线程组
         * bossGroup 仅处理连接请求
         * workerGroup 处理客户端业务
         * 两个组均为无限循环
         */
        // NioEventLoopGroup 不传参时默认 线程个数设置为 CPU * 2
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        // 服务器端的启动对象，用于配置参数
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 对服务器对象参数进行设置
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new NettyServerChannelInitializer());

        try {
            // 启动服务器
            ChannelFuture future = serverBootstrap.bind(8868).sync();
            System.out.println("服务器启动");
            // 对关闭通道进行监听
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            System.out.println("启动失败");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
