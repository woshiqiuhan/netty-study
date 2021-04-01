package cn.hznu.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIOServer {
    private final int port;

    public AIOServer(int port) {
        this.port = port;
        listen();
    }

    private void listen() {
        try {
            ExecutorService executorService = Executors.newCachedThreadPool();
            AsynchronousChannelGroup threadPool =
                    AsynchronousChannelGroup.withCachedThreadPool(executorService, 1);
            AsynchronousServerSocketChannel server =
                    AsynchronousServerSocketChannel.open(threadPool);
            server.bind(new InetSocketAddress(port));
            System.out.println("服务器端已启动，监听端口" + port);

            server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                final ByteBuffer buffer = ByteBuffer.allocate(1024);

                @Override
                public void completed(AsynchronousSocketChannel result, Object attachment) {
                    System.out.println("I/O 操作成功，开始读取数据");

                    try {
                        buffer.clear();
                        Integer len = result.read(buffer).get();
                        buffer.flip();
                        System.out.println("读取到到数据为：" + new String(buffer.array(), 0, len));
                    } catch (InterruptedException | ExecutionException e) {
                        System.out.println(e.getMessage());
                    }
                    try {
                        result.write(ByteBuffer.wrap("客户端你好，我已接收到".getBytes())).get();
                        System.out.println("操作完成");
                    } catch (InterruptedException | ExecutionException e) {
                        System.out.println(e.getMessage());
                    }
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.out.println("I/O 操作失败 " + exc.getMessage());
                }
            });
            Thread.sleep(Integer.MAX_VALUE);
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        new AIOServer(8086);
    }
}