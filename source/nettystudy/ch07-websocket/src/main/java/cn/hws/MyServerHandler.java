package cn.hws;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDateTime;

/**
 * WebSocketFrame 表示 WebSocket 传输的一个 文本帧 (frame)
 */
public class MyServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("接收到浏览器 " + ctx.channel().remoteAddress() + " 的请求");

        // 恢复消息
        ctx.channel().writeAndFlush(
                new TextWebSocketFrame("服务器时间 " + LocalDateTime.now() + msg.text()));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // asLongText 唯一，asShortText 不一定
        System.out.println("浏览器 " + ctx.channel().remoteAddress() + " 连接，" + ctx.channel().id().asLongText());
        System.out.println("浏览器 " + ctx.channel().remoteAddress() + " 连接，" + ctx.channel().id().asShortText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常发生");
        ctx.channel().close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // asLongText 唯一，asShortText 不一定
        System.out.println("浏览器 " + ctx.channel().remoteAddress() + " 断开连接，" + ctx.channel().id().asLongText());
    }
}
