package cn.qh.remoting.transport.server;


import cn.qh.enums.CompressTypeEnum;
import cn.qh.enums.RpcResponseCode;
import cn.qh.enums.SerializationTypeEnum;
import cn.qh.factory.SingletonFactory;
import cn.qh.remoting.constants.RpcConstants;
import cn.qh.remoting.dto.RpcMessage;
import cn.qh.remoting.dto.RpcRequest;
import cn.qh.remoting.dto.RpcResponse;
import cn.qh.remoting.handler.RpcRequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private final RpcRequestHandler rpcRequestHandler;

    public NettyServerHandler() {
        rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof RpcMessage) {
                // 初始化 response message
                RpcMessage message = new RpcMessage();
                message.setCoderType(SerializationTypeEnum.PROTOSTUFF.getCode());
                message.setCompressType(CompressTypeEnum.GZIP.getCode());

                // 获取请求类型
                byte type = ((RpcMessage) msg).getMessageType();
                if (type == RpcConstants.HEART_REQUEST) {
                    // 心跳检测
                    log.info("server receives heart beat : [{}]", RpcConstants.PING);
                    message.setMessageType(RpcConstants.HEART_RESPONSE);
                    message.setData(RpcConstants.PONG);
                } else if (type == RpcConstants.REQUEST_TYPE) {
                    // 调用服务请求
                    log.info("server receives message : [{}]", msg.toString());
                    message.setMessageType(RpcConstants.RESPONSE_TYPE);
                    // 获取请求数据
                    RpcRequest request = (RpcRequest) (((RpcMessage) msg).getData());
                    // 处理请求，反射 invoke
                    Object result = rpcRequestHandler.handle(request);
                    log.info("get result : [{}]", result.toString());
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        RpcResponse<Object> success = RpcResponse.success(result, request.getRequestId());
                        message.setData(success);
                    } else {
                        RpcResponse<Object> fail = RpcResponse.fail(RpcResponseCode.FAIL);
                        message.setData(fail);
                        log.error("not writable now");
                    }
                    log.info("server sends message : [{}]", message.toString());
                }
                ctx.channel().writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    // 心跳检测
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("idle check happen , so connection closed");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}