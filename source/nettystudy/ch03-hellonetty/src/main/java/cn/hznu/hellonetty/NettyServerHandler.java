package cn.hznu.hellonetty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 自定义 Handler
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 读取数据事件，读取客户端发送对数据
     *
     * @param ctx 上下文对象，可用于获取 pipeline channel
     * @param msg 客户端发送来对数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // ByteBuf 有 Netty 提供，
        System.out.println("客户端 "+ ctx.channel().remoteAddress()
                + "：" + ((ByteBuf) msg).toString(CharsetUtil.UTF_8));
    }

    /**
     * 数据读取完毕后调用
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 写入并刷新到缓存，即 write + flush
        // 通常需要对发送信息进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer(
                "客户端 " + ctx.channel().remoteAddress() + " 你好", CharsetUtil.UTF_8));
    }

    // 处理异常，一般用于关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
