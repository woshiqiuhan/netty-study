package cn.netty.tomcat;

import cn.netty.tomcat.http.GPRequest;
import cn.netty.tomcat.http.GPResponse;
import cn.netty.tomcat.http.GPServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class GPTomcat {
    private static final String ROOT_PATH = GPTomcat.class.getResource("/").getPath();

    // 存储 url -> servlet 的映射关系
    private final Map<String, GPServlet> servletMapping;

    private final Properties config;

    public GPTomcat(int port) {
        servletMapping = new HashMap<>();
        config = new Properties();
        init();

        // Netty 封装了 Reactor 模型， Boss、Worker
        EventLoopGroup bossGroup = new NioEventLoopGroup();  // Boss 线程池
        EventLoopGroup workerGroup = new NioEventLoopGroup();  // Worker 线程池

        try {
            // 创建服务器启动对象
            ServerBootstrap server = new ServerBootstrap();

            // 配置启动参数
            server.group(bossGroup, workerGroup)
                    // 主线程处理类
                    .channel(NioServerSocketChannel.class)
                    // 子线程处理类
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 初始化客户端
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 无锁化串行编程
                            // Netty 对 HTTP 对封装，对顺序有要求
                            // HttpResponseEncoder，编码器
                            // 责任链模式，双向链表 Inbound Outbound
                            ch.pipeline().addLast(new HttpResponseEncoder());
                            // HttpRequestDecoder，解码器
                            ch.pipeline().addLast(new HttpRequestDecoder());
                            // 业务逻辑处理
                            ch.pipeline().addLast(new GPTomcatHandler());
                        }
                    })
                    // 针对主线程对配置 分配线程最大数量 128
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 对子线程对配置，保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            //启动服务器
            ChannelFuture future = server.bind(port).sync();
            System.out.println("GPTomcat 已启动，监听的端口是：" + port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅地关闭线程池
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    // 处理 servlet mapping 映射
    private void init() {
        try {
            // 加载配置文件
            config.load(new FileInputStream(ROOT_PATH + "cn/netty/web.netty.properties"));
            for (Object o : config.keySet()) {
                String key = o.toString();
                // 处理映射关系
                if (key.endsWith(".url")) {
                    String servletName = key.split("\\.")[1];
                    String url = config.getProperty(key);
                    String className = config.getProperty("servlet." + servletName + ".class");
                    GPServlet servlet = (GPServlet) Class.forName(className).newInstance();
                    servletMapping.put(url, servlet);
                }
            }
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private class GPTomcatHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpRequest) {
                System.out.println("接收到请求：" + msg);
                HttpRequest request = (HttpRequest) msg;
                GPRequest gpRequest = new GPRequest(request);
                GPResponse gpResponse = new GPResponse(ctx);

                // 处理业务
                String url = gpRequest.getUrl();
                if (servletMapping.containsKey(url)) {
                    servletMapping.get(url).service(gpRequest, gpResponse);
                } else {
                    gpResponse.write("<h1>404 - Not Found</h1>");
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }
    }

    public static void main(String[] args) {
        new GPTomcat(9895);
    }
}
