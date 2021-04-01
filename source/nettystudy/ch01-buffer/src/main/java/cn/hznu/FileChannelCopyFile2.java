package cn.hznu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileChannelCopyFile2 {
    public static void main(String[] args) throws IOException {
        File file = new File("/Users/qiuhan/desktop/1.txt");
        // 打开文件输入流对象
        FileInputStream fis = new FileInputStream(file);
        // 打开文件输出流
        FileOutputStream fos = new FileOutputStream("/Users/qiuhan/desktop/4.txt");

        // 获得 FileChannel 对象
        FileChannel fosChannel = fos.getChannel();
        FileChannel fisChannel = fis.getChannel();

        // 简便写法，将拷贝过程封装
        // 将目标通道中数据拷贝到当前通道
        fosChannel.transferFrom(fisChannel, 0, fisChannel.size());
        // 将当前通道中数据拷贝到目标通道中
        fisChannel.transferTo(0, fisChannel.size(), fosChannel);
        fis.close();
        fos.close();
    }
}
