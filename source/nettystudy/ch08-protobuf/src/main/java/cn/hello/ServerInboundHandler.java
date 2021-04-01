package cn.hello;

import cn.hello.protobuf.StudentOuter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端 " + ctx.channel().remoteAddress() + " 连接");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.print("客户端说：");
        StudentOuter.Student student = (StudentOuter.Student) msg;

        System.out.println(student.getId() + " " + student.getName());
    }
}
