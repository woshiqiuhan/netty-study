package cn.hznu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelCopyFile {
    public static void main(String[] args) throws IOException {
        File file = new File("/Users/qiuhan/desktop/1.txt");
        // 打开文件输入流对象
        FileInputStream fis = new FileInputStream(file);
        // 打开文件输出流
        FileOutputStream fos = new FileOutputStream("/Users/qiuhan/desktop/2.txt");

        // 获得 FileChannel 对象
        FileChannel fosChannel = fos.getChannel();
        FileChannel fisChannel = fis.getChannel();

        // 创建 缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (true) {
            // 清空缓冲区
            buffer.clear();
            // 读入文件信息
            int read = fisChannel.read(buffer);
            if (read == -1) {
                break;
            }
            buffer.flip();
            // 写入
            fosChannel.write(buffer);
        }

        fis.close();
        fos.close();
    }
}
