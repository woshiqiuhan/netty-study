package cn.qh.proxy;

import cn.qh.entity.RpcServiceProperties;
import cn.qh.enums.RpcResponseCode;
import cn.qh.remoting.dto.RpcRequest;
import cn.qh.remoting.dto.RpcResponse;
import cn.qh.remoting.transport.RpcRequestTransport;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("all")
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private final RpcRequestTransport rpcClient;
    private final RpcServiceProperties rpcServiceProperties;

    public RpcClientProxy(RpcRequestTransport rpcClient, RpcServiceProperties rpcServiceProperties) {
        this.rpcClient = rpcClient;
        if (rpcServiceProperties.getGroup() == null)
            rpcServiceProperties.setGroup("");
        if (rpcServiceProperties.getVersion() == null)
            rpcServiceProperties.setVersion("");
        this.rpcServiceProperties = rpcServiceProperties;
    }

    public <T> T getProxy(Class<T> tClass) {
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class<?>[]{tClass}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .args(args)
                .argsType(method.getParameterTypes())
                .group(rpcServiceProperties.getGroup())
                .version(rpcServiceProperties.getGroup())
                .build();
        CompletableFuture<RpcResponse<Object>> completableFuture = (CompletableFuture<RpcResponse<Object>>) rpcClient.sendRequest(rpcRequest);
        RpcResponse<Object> rpcResponse = completableFuture.get();
        check(rpcRequest, rpcResponse);
        return rpcResponse.getData();
    }

    private void check(RpcRequest rpcRequest, RpcResponse<Object> rpcResponse) {
        if (rpcResponse == null) {
            log.error("client calls method failed");
            throw new RuntimeException();
        }

        if (rpcResponse.getCode() == null || rpcResponse.getCode() != RpcResponseCode.SUCCESS.getCode()) {
            log.error("server returns response failed");
            throw new RuntimeException();
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            log.error("requestId isn't equal to responseId");
            throw new RuntimeException();
        }
    }
}
