package cn.hznu.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;

public class AIOClient {
    private AsynchronousSocketChannel client;

    public AIOClient() throws IOException {
        this.client = AsynchronousSocketChannel.open();
        System.out.println("客户端初始化完成");
    }

    private void connect(String host, int port) {
        client.connect(new InetSocketAddress(host, port), null,
                new CompletionHandler<Void, Object>() {
                    @Override
                    public void completed(Void result, Object attachment) {
                        try {
                            client.write(ByteBuffer.wrap("服务端你好，这是一条测试数据".getBytes())).get();
                            System.out.println("消息已发送至服务器");
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        // 读事件在 connect 外面可能会导致 java.nio.channels.NotYetConnectedException 异常
                        // 原因是因为异步，可能还未 connect 完就开始读
                        final ByteBuffer buffer = ByteBuffer.allocate(1024);
                        client.read(buffer, null, new CompletionHandler<Integer, Object>() {
                            @Override
                            public void completed(Integer result, Object attachment) {
                                System.out.println("获取反馈结果：" + new String(buffer.array(), 0, result));
                            }

                            @Override
                            public void failed(Throwable exc, Object attachment) {
                                System.out.println("I/O 操作失败 " + exc.getMessage());
                            }
                        });
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        System.out.println("I/O 操作失败 " + exc.getMessage());
                    }
                });
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            System.out.println("I/O 操作失败 " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        new AIOClient().connect("127.0.0.1", 8086);
    }
}