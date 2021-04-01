package cn.bio.tomcat;

import cn.bio.tomcat.http.GPRequest;
import cn.bio.tomcat.http.GPResponse;
import cn.bio.tomcat.http.GPServlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("GPTomcat 已启动，监听的端口是：" + port);

            // 等待用户发送的请求
            while (true) {
                Socket socket = serverSocket.accept();
                handler(socket);
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        try {
            // 加载配置文件
            config.load(new FileInputStream(ROOT_PATH + "cn/bio/web.bio.properties"));
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

    private void handler(Socket socket) {
        try (InputStream in = socket.getInputStream();
             OutputStream os = socket.getOutputStream()) {
            // 初始化请求头和相应体
            GPRequest request = new GPRequest(in);
            GPResponse response = new GPResponse(os);

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
        new GPTomcat(9893);
    }
}
