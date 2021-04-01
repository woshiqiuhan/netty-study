package cn.qiu.codec;

import cn.qiu.protocol.RpcMessage;
import cn.qiu.serialize.Protostuff.ProtostuffSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder<RpcMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) throws Exception {
        byte[] serialize = ProtostuffSerializer.serialize(msg);
        out.writeInt(serialize.length);
        out.writeBytes(serialize);
    }
}
