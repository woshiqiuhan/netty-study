package cn.hznu.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    public static void main(String[] args) {
        // 创建 ServerSocketChannel Selector 对象
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open();) {
            // 绑定端口，服务端监听
            serverSocketChannel.socket().bind(new InetSocketAddress(8868));

            // 设置为非阻塞
            serverSocketChannel.configureBlocking(false);

            // 将 selector 注册到 serverSocketChannel 中，关注事件 OP_ACCEPT 为接收连接
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            // 循环等待客户端连接
            while (true) {
                if (selector.select(1000) == 0) {
                    System.out.println("1s 内无连接");
                    continue;
                }
                // 获取 SelectionKey 集合，发生事件的通道的 SelectionKey
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                // keys() 方法获取注册的所有通道
                Set<SelectionKey> keys = selector.keys();

                // 遍历集合
                Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    // 根据 key 对应通道发生事件做出相应处理
                    if (key.isAcceptable()) { // 发生 OP_ACCEPT 事件
                        // 新的客户端连接
                        // 设置为非阻塞状态后，若无连接则会返回null
                        // 没有设置为非阻塞状态则 accept 为阻塞方法
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        // 将 socketChannel 设置为非阻塞
                        socketChannel.configureBlocking(false);
                        // 将 socketChannel 注册到 selector 中，关注事件 OP_READ 为读事件
                        // 注册时为通道绑定一个 buffer
                        socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    }
                    if (key.isReadable()) { // 发生 OP_READ 事件
                        readMsg(key);
                    }
                    // 从集合中删除 key，防止重复操作
                    keyIterator.remove();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readMsg(SelectionKey key) {
        // 通过 key 反向获取 socketChannel
        try (SocketChannel socketChannel = (SocketChannel) key.channel();) {
            // 获取相应的 buffer
            ByteBuffer buffer = (ByteBuffer) key.attachment();
            int read = socketChannel.read(buffer);
            buffer.flip();
            // 注：用于读入的字节数组大小应与 缓冲区读入的内容字节大小相同
            byte[] bytes = new byte[buffer.remaining()];
            // 从缓冲区获取
            buffer.get(bytes);
            System.out.println(socketChannel + " 读取字节数 read = " + read + " 内容 = " + new String(bytes));
        } catch (IOException e) {
            System.out.println("用户已断开连接");
            // 取消注册
            key.cancel();
        }
    }
}