package cn.hznu;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * scattering: 将数据写入到 buffer 时可以采用 buffer 数组依次写入
 * gathering: 将 buffer 数据读取数据时也可以采用 buffer 数组依次读入
 */
public class ScatteringAndGatheringTest2 {
    public static void main(String[] args) throws IOException {
        // 使用 ServerSocketChannel 和 SocketChannel 网络
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(8868);
        serverSocketChannel.socket().bind(address);

        ByteBuffer[] buffers = new ByteBuffer[2];
        buffers[0] = ByteBuffer.allocate(5);
        buffers[1] = ByteBuffer.allocate(3);

        // 等待客户端连接
        SocketChannel socketChannel = serverSocketChannel.accept();
        int messageLength = 8;
        // 循环读取
        while (true) {
            System.out.println("Read: ");
            long byteRead = 0;
            while (byteRead < messageLength) {
                byteRead += socketChannel.read(buffers);
                System.out.println(byteRead);
                Arrays.stream(buffers)
                        .map(byteBuffer -> "position = " + byteBuffer.position()
                                + " limit = " + byteBuffer.limit())
                        .forEach(System.out::println);
            }
            // 将所有 buffer 进行反转
            Arrays.asList(buffers).forEach(Buffer::flip);

            // 将数据写入到客户端
            System.out.println("Write: ");
            long byteWrite = 0;
            while (byteWrite < messageLength) {
                byteWrite += socketChannel.write(buffers);
                System.out.println(byteWrite);
                Arrays.stream(buffers)
                        .map(byteBuffer -> "position = " + byteBuffer.position()
                                + " limit = " + byteBuffer.limit())
                        .forEach(System.out::println);
            }
            // 将所有 buffer clean
            Arrays.asList(buffers).forEach(ByteBuffer::clear);

            System.out.println("byteWrite = " + byteWrite + " byteRead = " + byteRead);
        }
    }
}

/**
 * 使用 telnet 工具测试
 * telnet 127.0.0.1 7000
 * send message
 */