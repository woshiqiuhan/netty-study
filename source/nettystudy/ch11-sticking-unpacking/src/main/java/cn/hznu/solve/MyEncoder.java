package cn.hznu.solve;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

public class MyEncoder extends MessageToByteEncoder<MyProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MyProtocol msg, ByteBuf out) throws Exception {
        System.out.println("MyEncoder 方法被调用");

        out.writeInt(msg.getLen());
        out.writeBytes(msg.getMsg().getBytes(CharsetUtil.UTF_8));
    }
}
