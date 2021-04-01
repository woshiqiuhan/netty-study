package cn.hznu.pipe;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

public class Test {
    public static void main(String[] args) throws IOException {
        Pipe pipe = Pipe.open();
        Pipe.SinkChannel sink = pipe.sink();
        Pipe.SourceChannel source = pipe.source();

        ByteBuffer buffer = ByteBuffer.wrap("Hello pipe!".getBytes());
        sink.write(buffer);

        buffer = ByteBuffer.allocate(1024);
        System.out.println(new String(buffer.array()));
        int read = source.read(buffer);
        System.out.println(new String(buffer.array(), 0, read));
    }
}
