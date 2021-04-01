package cn.hznu.niochatroom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private static final int PORT = 8868;

    public Server() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            selector = Selector.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("服务端初始化完成");
        } catch (IOException e) {
            System.out.println("服务器初始化失败");
        }
    }

    public void listen() {
        while (true) {
            try {
                int cnt = selector.select(2000);
                if (cnt > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        // 新用户上线
                        if (key.isAcceptable()) {
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            String msg = "用户" + socketChannel.getRemoteAddress() + "上线了";
                            System.out.println(msg);
                            forwordMsg(msg, null);
                        }
                        if (key.isReadable()) {
                            // 获取用户端发出读数据并转发
                            readMsg(key);
                        }
                        // 手动删除当前 key 防止重复读
                        iterator.remove();
                    }
                } else {
                    // System.out.println("等待客户端连接");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取数据
     */
    private void readMsg(SelectionKey key) {
        // 通过 key 获取 socketChannel
        SocketChannel socketChannel = null;
        try {
            socketChannel = (SocketChannel) key.channel();
            // 获取相应的 buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            if (socketChannel.read(buffer) == -1) {
                key.cancel();
                System.out.println("用户" + socketChannel.getRemoteAddress() + "断开连接");
                try {
                    forwordMsg("用户" + socketChannel.getRemoteAddress() + "断开连接", null);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                return;
            }
            buffer.flip();
            // 注：用于读入的字节数组大小应与 缓冲区读入的内容字节大小相同
            byte[] bytes = new byte[buffer.remaining()];
            // 从缓冲区获取
            buffer.get(bytes);
            String msg = "用户" + socketChannel.getRemoteAddress() + "：" + new String(bytes);
            System.out.println(msg);
            forwordMsg(msg, socketChannel);
        } catch (IOException e) {
            // 取消注册
            key.cancel();
        }
    }

    /**
     * 转发数据
     */
    private void forwordMsg(String msg, SocketChannel socketChannel) {
        selector.keys().forEach(key -> {
            if (key.channel() instanceof SocketChannel) {
                SocketChannel channel = (SocketChannel) key.channel();
                try {
                    channel.configureBlocking(false);

                    if (channel != socketChannel) {
                        ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
                        channel.write(byteBuffer);
                    }
                } catch (IOException e) {
                    // 取消注册
                    key.cancel();
                }
            }
        });
    }

    public static void main(String[] args) throws IOException {
        new Server().listen();
    }
}