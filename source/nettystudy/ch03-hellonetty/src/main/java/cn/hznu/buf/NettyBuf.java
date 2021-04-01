package cn.hznu.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 认识 netty 提供对 ByteBuf
 * 内部是一个 byte[]，其大小为 capacity
 * 底层维护了 readerIndex 和 writerIndex，顾不需要进行反转操作
 * writerIndex 表示下一个要写入对位置(针对 byte[])
 * readerIndex 代表 readByte() 方法调用将读对下一个位置，使用 getByte(index) 不会改变 readerIndex 的值
 * capacity readerIndex writerIndex 对大小关系如下所示
 * 0 <= readerIndex <= writerIndex <= capacity
 */
public class NettyBuf {
    public static void main(String[] args) {
        // Unpooled.copiedBuffer("Hey, 我是三".getBytes(), )
        // 创建制定大小对 ByteBuf，内部是一个 byte[]
        ByteBuf buf = Unpooled.buffer(15);

        // 将数据存入缓冲区
        for (int i = 0; i < 10; i++) {
            buf.writeByte(i);
        }

        // 读取缓冲区数据，切换使用场景时不需要 flip 反转
        // for (int i = 0; i < buf.writerIndex(); i++) {
        //     System.out.print(buf.getByte(i) + " ");
        // }
        // System.out.println();
        while (buf.isReadable()) {
            System.out.print(buf.readByte() + " ");
        }
        System.out.println();
        buf.writeByte(11);
        for (int i = 0; i < buf.writerIndex(); i++) {
            System.out.print(buf.getByte(i) + " ");
        }
        System.out.println();
        System.out.println(buf.readByte());
    }
}
