package cn.hznu.protobuf.consumer.proxy;

import cn.hznu.protobuf.protocol.RpcMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RpcProxyHandler extends ChannelInboundHandlerAdapter {
    private Object response;

    public Object getResponse() {
        return response;
    }

    // 获取远程调用返回的结果
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        response = ((RpcMessage) msg).getRpcMessage();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}