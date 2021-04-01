package cn.hznu.niochatroom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Client {
    private SocketChannel socketChannel;
    private Selector selector;
    private static final String HOST_NAME = "127.0.0.1";
    private static final int PORT = 8868;

    public Client() {
        // 得到网络通道
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(HOST_NAME, PORT);

            socketChannel.register(selector, SelectionKey.OP_READ);
            if (!socketChannel.connect(inetSocketAddress)) {
                // 连接成功
                while (!socketChannel.finishConnect()) {
                    // 若果还未完成连接操作
                    System.out.println("连接中");
                }
            }
            System.out.println("客户端初始化成功");
            // System.out.println(socketChannel.getLocalAddress().toString() + "登陆成功");
        } catch (IOException e) {
            System.out.println("客户端初始化失败");
        }
    }

    private void sendMsg(String msg) {
        if (msg == null || msg.length() == 0) {
            return;
        }
        try {
            socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
            System.out.println("我：" + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMsg() {
        while (true) {
            try {
                int cnt = selector.select(1000);
                if (cnt > 0) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if (key.isReadable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            channel.read(buffer);
                            buffer.flip();
                            // 注：用于读入的字节数组大小应与 缓冲区读入的内容字节大小相同
                            byte[] bytes = new byte[buffer.remaining()];
                            // 从缓冲区获取
                            buffer.get(bytes);
                            System.out.println(new String(bytes));
                        }
                        iterator.remove();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.readMsg();
            }
        }).start();
        while (true) {
            Scanner in = new Scanner(System.in);
            client.sendMsg(in.nextLine());
        }
    }
}