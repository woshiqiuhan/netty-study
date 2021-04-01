package cn.hznu;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelWriteFile {
    public static void main(String[] args) throws Exception {
        // 打开文件输出流
        FileOutputStream fos = new FileOutputStream("/Users/qiuhan/desktop/1.txt");

        // 获取 FileChannel 对象
        FileChannel channel = fos.getChannel();

        // 写入需要通过 Buffer 缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("Hello, 秋寒".getBytes());

        // 处理好缓冲区内容后进行 读写翻转
        buffer.flip();

        // 将缓冲区内容写入 FileChannel
        channel.write(buffer);
        fos.close();
    }
}
