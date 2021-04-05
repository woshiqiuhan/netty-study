package cn.hznu.protostuff.codec;

import cn.hznu.protobuf.protocol.InvokerProtocol;
import cn.hznu.protobuf.serialize.Protostuff.ProtostuffSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder<InvokerProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, InvokerProtocol msg, ByteBuf out) throws Exception {
        byte[] serialize = ProtostuffSerializer.serialize(msg);
        out.writeInt(serialize.length);
        out.writeBytes(serialize);
    }
}
