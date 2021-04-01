package cn.hznu.zerocopy;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NewClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8868));

        FileInputStream fis = new FileInputStream("/Users/qiuhan/Desktop/3.zip");
        FileChannel channel = fis.getChannel();

        int n = (int) Math.ceil((double) channel.size() / 8388608.0);
        long s = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            channel.transferTo(i * 8388608L, Math.min(channel.size(), (i + 1) * 8388608L), socketChannel);
        }
        System.out.println("传输完成，共传输" + channel.size() + "字节，用时" + (System.currentTimeMillis() - s) + "ms");
    }
}
