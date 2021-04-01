package cn.nio.tomcat.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class GPResponse {
    private SocketChannel socketChannel;

    public GPResponse(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void write(String s) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(("HTTP/1.1 200 OK\n" +
                "Content-Type: text/html;\n" +
                "\r\n" +
                s).getBytes());
        socketChannel.write(buffer);
    }
}
