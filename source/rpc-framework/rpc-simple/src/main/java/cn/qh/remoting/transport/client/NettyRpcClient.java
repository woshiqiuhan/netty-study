package cn.qh.remoting.transport.client;

import cn.qh.enums.CompressTypeEnum;
import cn.qh.enums.SerializationTypeEnum;
import cn.qh.extension.ExtensionLoader;
import cn.qh.factory.SingletonFactory;
import cn.qh.register.ServiceDiscovery;
import cn.qh.remoting.constants.RpcConstants;
import cn.qh.remoting.dto.RpcMessage;
import cn.qh.remoting.dto.RpcRequest;
import cn.qh.remoting.dto.RpcResponse;
import cn.qh.remoting.transport.RpcRequestTransport;
import cn.qh.remoting.transport.coder.RpcMessageDecoder;
import cn.qh.remoting.transport.coder.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class NettyRpcClient implements RpcRequestTransport {

    private final Bootstrap bootstrap;
    private final ChannelProvider channelProvider;
    private final UnprocessedRequests unprocessedRequests;
    private final ServiceDiscovery serviceDiscovery;

    public NettyRpcClient() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new IdleStateHandler(0, 15, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new RpcMessageEncoder());
                        ch.pipeline().addLast(new RpcMessageDecoder());
                        ch.pipeline().addLast(new NettyRpcClientHandler());
                    }
                });
        channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
    }

    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("The client has connected [{}] successfully!", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            }
        });
        return completableFuture.get();
    }


    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        CompletableFuture<RpcResponse<Object>> completableFuture = new CompletableFuture<>();
        String rpcServiceName = rpcRequest.toRpcServiceProperties().toRpcServiceName();

        // 从服务中心获取服务地址
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcServiceName);

        // 出 bug
        // 获取对应的channel
        Channel channel = getChannel(inetSocketAddress);

        if (channel.isActive()) {
            unprocessedRequests.put(rpcRequest.getRequestId(), completableFuture);
            RpcMessage rpcMessage = new RpcMessage();
            rpcMessage.setData(rpcRequest);
            rpcMessage.setCoderType(SerializationTypeEnum.PROTOSTUFF.getCode());
            rpcMessage.setCompressType(CompressTypeEnum.GZIP.getCode());
            rpcMessage.setMessageType(RpcConstants.REQUEST_TYPE);
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message : [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    completableFuture.completeExceptionally(future.cause());
                    log.error("send failed : ", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }
        return completableFuture;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            System.out.println(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }
}
