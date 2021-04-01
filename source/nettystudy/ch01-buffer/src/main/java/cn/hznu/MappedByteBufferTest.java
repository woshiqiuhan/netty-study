package cn.hznu;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MappedByteBufferTest {
    public static void main(String[] args) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/qiuhan/desktop/1.txt", "rw");

        FileChannel channel = randomAccessFile.getChannel();
        /**
         * 使得文件在内存(堆外内存)中直接修改，操作系统不需要拷贝一份，效率高
         */
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

        mappedByteBuffer.put(0, (byte) 'H');
        randomAccessFile.close();

    }
}
