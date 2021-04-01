package cn.hznu.solve;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

public class ClientChannelHandler extends SimpleChannelInboundHandler<MyProtocol> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyProtocol msg) throws Exception {


        System.out.println("服务端说：" + msg.getMsg());
        System.out.println();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 15; i++) {
            MyProtocol myProtocol = new MyProtocol();
            myProtocol.setMsg("服务端你好" + i);
            myProtocol.setLen(myProtocol.getMsg().getBytes().length);
            ctx.writeAndFlush(myProtocol);
        }
    }

    public static void main(String[] args) {

    }
}
