package cn.hznu.solve;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

public class ServerChannelHandler extends SimpleChannelInboundHandler<MyProtocol> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyProtocol msg) throws Exception {
        System.out.println("客户端说；" + msg.getMsg());
        System.out.println();

        MyProtocol myProtocol = new MyProtocol();
        myProtocol.setMsg("客户端你好");
        myProtocol.setLen(myProtocol.getMsg().getBytes().length);
        ctx.writeAndFlush(myProtocol);
    }

}
