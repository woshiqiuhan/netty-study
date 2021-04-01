package cn.hznu.zerocopy;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NewServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(8868));

        ByteBuffer buffer = ByteBuffer.allocate(1024);


        FileChannel channel = new FileOutputStream("/Users/qiuhan/Desktop/4.zip").getChannel();
        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            /*int len = 0;
            while ((len = socketChannel.read(buffer)) != -1) {
                buffer.clear();
            }*/
            long s = System.currentTimeMillis();
            channel.transferFrom(socketChannel, 0, 5298286);
            System.out.println("传输完成，共传输" + channel.size() + "字节，用时" + (System.currentTimeMillis() - s) + "ms");
        }
    }
}
