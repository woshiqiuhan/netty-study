package cn.hznu;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelReadFile {
    public static void main(String[] args) throws Exception {
        File file = new File("/Users/qiuhan/desktop/1.txt");
        // 打开文件输入流对象
        FileInputStream fis = new FileInputStream(file);
        // 获取 FileChannel 对象
        FileChannel channel = fis.getChannel();

        // 创建读入缓冲区
        ByteBuffer buffer = ByteBuffer.allocate((int) file.length());

        // 从 FileChannel 中读入信息
        channel.read(buffer);

        System.out.println(new String(buffer.array()));
        /*// 读写翻转
        buffer.flip();
        // 注：用于读入的字节数组大小应与 缓冲区读入的内容字节大小相同
        byte[] bytes = new byte[buffer.remaining()];
        // 存入缓冲区
        buffer.get(bytes);
        System.out.println(new String(bytes));*/
        fis.close();
    }
}
