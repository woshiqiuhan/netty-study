package cn.hznu.solve;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

import java.util.List;

public class MyDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("MyDecoder 方法被调用");

        int len = in.readInt();
        byte[] bytes = new byte[len];
        in.readBytes(bytes);

        MyProtocol protocol = new MyProtocol();
        protocol.setLen(len);
        protocol.setMsg(new String(bytes, CharsetUtil.UTF_8));
        out.add(protocol);
    }
}
