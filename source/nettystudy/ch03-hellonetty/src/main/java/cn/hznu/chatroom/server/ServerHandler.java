package cn.hznu.chatroom.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;

public class ServerHandler extends SimpleChannelInboundHandler<String> {

    // ChannelGroup，用于管理所有客户端连接的 channel
    // GlobalEventExecutor.INSTANCE：全局的事件执行器，单例
    public static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 连接建立时触发
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // 首先将当前用户上线通知到所有其它客户端
        /*
          ChannelGroup 封装了相应方法
          ChannelGroup.writeAndFlush 会遍历当前 ChannelGroup 中所有 channel 并发送消息
         */
        channels.writeAndFlush("[广播] " + channel.remoteAddress() + " 加入聊天室！");

        // 将当前 channel 加入到 ChannelGroup 中
        channels.add(channel);
    }

    // 连接断开时触发
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        /*
            从 ChannelGroup 中移出
            channels.remove(channel);
            handlerRemoved 即当前方法到执行会自动从 ChannelGroup 移出断开连接到 channel
         */
        channels.writeAndFlush("[广播] " + channel.remoteAddress() + " 离开聊天室！");
    }

    // 表示 channel 处于活跃状态，上线通知
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[广播] " + ctx.channel().remoteAddress() + " 上线！");
    }

    // 表示 channel 处于离线状态，下线通知
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[广播] " + ctx.channel().remoteAddress() + " 下线！");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        String str = "[用户] " + channel.remoteAddress() + "：" + msg;
        System.out.println(str);
        channels.forEach(c -> {
            if (c != channel) {
                c.writeAndFlush(str);
            } else {
                c.writeAndFlush("[用户] " + "我：" + msg);
            }
        });
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}