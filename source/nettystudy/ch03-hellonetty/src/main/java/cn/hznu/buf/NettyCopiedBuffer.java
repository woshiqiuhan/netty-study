package cn.hznu.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class NettyCopiedBuffer {
    public static void main(String[] args) {
        ByteBuf byteBuf = Unpooled.copiedBuffer("HeyMan", CharsetUtil.UTF_8);

        // ByteBuf.hasArray() 方法，返回是否以及给缓冲区分配了 byte[]
        System.out.println(byteBuf.capacity());

        System.out.println(new String(byteBuf.array(), 0,
                byteBuf.writerIndex(), CharsetUtil.UTF_8));
    }
}
