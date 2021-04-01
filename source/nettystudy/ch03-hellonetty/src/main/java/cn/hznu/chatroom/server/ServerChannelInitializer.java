package cn.hznu.chatroom.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 获取 pipeline
        ChannelPipeline pipeline = ch.pipeline();

        // 加入解码器
        pipeline.addLast("decoder", new StringDecoder());
        // 加入编码器
        pipeline.addLast("encoder", new StringEncoder());

        // 加入自定义 handler
        pipeline.addLast("serverHandler", new ServerHandler());
    }
}