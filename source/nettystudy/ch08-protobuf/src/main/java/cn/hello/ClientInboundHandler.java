package cn.hello;

import cn.hello.protobuf.StudentOuter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientInboundHandler extends ChannelInboundHandlerAdapter {
    // 注：启动触发对是 channelActive 而不是 handlerAdded
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端连接成功");
        StudentOuter.Student student =
                StudentOuter.Student.newBuilder().setId(1).setName("秋寒").build();
        ctx.writeAndFlush(student);

        // ctx.writeAndFlush(Unpooled.copiedBuffer("Hey man", CharsetUtil.UTF_8));
    }
}
