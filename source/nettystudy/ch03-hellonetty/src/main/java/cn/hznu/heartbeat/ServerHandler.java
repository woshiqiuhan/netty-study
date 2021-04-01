package cn.hznu.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;

            String event = null;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    event = "读空闲";
                    break;
                case WRITER_IDLE:
                    event = "读空闲";
                    break;
                case ALL_IDLE:
                    event = "读写空闲";
                    break;
            }
            System.out.println(ctx.channel().remoteAddress() + " " + event);
            System.out.println("服务器做相应处理");
        }
    }
}
