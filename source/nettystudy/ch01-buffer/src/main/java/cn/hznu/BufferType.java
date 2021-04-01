package cn.hznu;

import java.nio.ByteBuffer;

public class BufferType {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(64);

        buffer.putChar('邱');
        buffer.putDouble(3.2312);

        buffer.flip();
        ByteBuffer byteBuffer = buffer.asReadOnlyBuffer();

        System.out.println(byteBuffer.getChar());
    }
}
