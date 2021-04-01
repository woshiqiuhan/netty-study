package cn.hznu.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {
    public static void main(String[] args) throws IOException {
        // 得到网络通道
        SocketChannel socketChannel = SocketChannel.open();
        // 设置为非阻塞
        socketChannel.configureBlocking(false);
        // 提供对应服务器 Ip port
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8868);
        if (!socketChannel.connect(inetSocketAddress)) {
            // 连接成功
            while (!socketChannel.finishConnect()) {
                // 若果还未完成连接操作
                System.out.println("连接中");
            }
        }

        // 连接完成，想服务端发送数据
        // warp 方法用于生成与发送数据等大大缓冲区 buffer
        ByteBuffer buffer = ByteBuffer.wrap("Hello, NIO!".getBytes());

        socketChannel.write(buffer);
        // System.in.read();
        System.out.println("客户端发送完成");
    }
}
