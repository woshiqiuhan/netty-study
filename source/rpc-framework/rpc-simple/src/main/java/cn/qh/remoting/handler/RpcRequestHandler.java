package cn.qh.remoting.handler;

import cn.qh.factory.SingletonFactory;
import cn.qh.provider.ServiceProvider;
import cn.qh.provider.ServiceProviderImpl;
import cn.qh.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// 接收服务端处理请求，反射机制
@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.toRpcServiceProperties());
        return invokeMethod(rpcRequest, service);
    }

    private Object invokeMethod(RpcRequest rpcRequest, Object service) {
        Object res = null;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getArgsType());
            res = method.invoke(service, rpcRequest.getArgs());
            log.info("[{}] invoke method [{}] successfully", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("invoke method error");
            e.printStackTrace();
        }
        return res;
    }
}
