package cn.nio.tomcat;

import cn.nio.tomcat.http.GPRequest;
import cn.nio.tomcat.http.GPResponse;
import cn.nio.tomcat.http.GPServlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class GPTomcat {
    private static final String ROOT_PATH = GPTomcat.class.getResource("/").getPath();
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    // 存储 url -> servlet 的映射关系
    private final Map<String, GPServlet> servletMapping;

    private final Properties config;

    public GPTomcat(int port) {
        servletMapping = new HashMap<>();
        config = new Properties();
        init();

        try {
            this.serverSocketChannel = ServerSocketChannel.open();
            selector = Selector.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("GPTomcat 已启动，监听的端口是：" + port);

            // 等待用户发送的请求
            listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listen() {
        while (true) {
            try {
                if (selector.select(2000) > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if (key.isAcceptable()) {
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        }
                        if (key.isReadable()) {
                            // 通过 key 反向获取 socketChannel
                            try (SocketChannel socketChannel = (SocketChannel) key.channel()) {
                                handler(socketChannel);
                            } catch (IOException e) {
                                System.out.println("用户已断开连接");
                                // 取消注册
                                key.cancel();
                            }
                        }
                        iterator.remove();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {
        try {
            // 加载配置文件
            config.load(new FileInputStream(ROOT_PATH + "cn/nio/web.nio.properties"));
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

    private void handler(SocketChannel socketChannel) {
        // 获取相应的 buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int read = 0;
        try {
            read = socketChannel.read(buffer);

            // 初始化请求头和相应体
            GPRequest request = new GPRequest(new String(buffer.array(), 0, read).split("\\n")[0]);
            GPResponse response = new GPResponse(socketChannel);

            String url = request.getUrl();
            if (servletMapping.containsKey(url)) {
                servletMapping.get(url).service(request, response);
            } else {
                response.write("<h1>404 - Not Found</h1>");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new GPTomcat(9894);
    }
}
