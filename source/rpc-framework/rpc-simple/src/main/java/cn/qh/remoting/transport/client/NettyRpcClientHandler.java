package cn.qh.remoting.transport.client;


import cn.qh.enums.CompressTypeEnum;
import cn.qh.enums.SerializationTypeEnum;
import cn.qh.factory.SingletonFactory;
import cn.qh.remoting.constants.RpcConstants;
import cn.qh.remoting.dto.RpcMessage;
import cn.qh.remoting.dto.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter {

    private final UnprocessedRequests unprocessedRequests;
    private final NettyRpcClient nettyRpcClient;

    public NettyRpcClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.nettyRpcClient = SingletonFactory.getInstance(NettyRpcClient.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof RpcMessage) {
                RpcMessage message = (RpcMessage) msg;
                byte type = message.getMessageType();
                if (type == RpcConstants.HEART_RESPONSE) {
                    log.info("client receives heart beat : [{}]", RpcConstants.PONG);
                } else if (type == RpcConstants.RESPONSE_TYPE) {
                    log.info("client receives message : [{}]", msg.toString());
                    unprocessedRequests.complete((RpcResponse<Object>) message.getData());
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 心跳包由客户端发出
        if (evt instanceof IdleStateEvent) {
            RpcMessage message = RpcMessage.builder()
                    .coderType(SerializationTypeEnum.PROTOSTUFF.getCode())
                    .compressType(CompressTypeEnum.GZIP.getCode())
                    .messageType(RpcConstants.HEART_REQUEST)
                    .data(RpcConstants.PING)
                    .build();
            Channel channel = nettyRpcClient.getChannel((InetSocketAddress) ctx.channel().remoteAddress());
            channel.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("client catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}


