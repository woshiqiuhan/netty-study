package cn.hznu.chatroom.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 获取 pipeline
        ChannelPipeline pipeline = ch.pipeline();

        // 加入解码器
        pipeline.addLast("decoder", new StringDecoder());
        // 加入编码器
        pipeline.addLast("encoder", new StringEncoder());

        pipeline.addLast("clientHandler", new ClientHandler());
    }
}