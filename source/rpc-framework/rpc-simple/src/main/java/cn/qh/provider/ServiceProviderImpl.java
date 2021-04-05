package cn.qh.provider;

import cn.qh.entity.RpcServiceProperties;
import cn.qh.extension.ExtensionLoader;
import cn.qh.register.ServiceRegister;
import cn.qh.remoting.transport.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 提供服务
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {

    // 服务与服务具体对象的映射
    private static final Map<String, Object> SERVICE_MAP = new ConcurrentHashMap<>();
    private final ServiceRegister SERVICE_REGISTER;

    public ServiceProviderImpl() {
        SERVICE_REGISTER = ExtensionLoader.getExtensionLoader(ServiceRegister.class).getExtension("zk");
    }

    @Override
    public void addService(Object service, RpcServiceProperties properties) {
        String serviceName = properties.toRpcServiceName();
        if (SERVICE_MAP.containsKey(serviceName))
            return;
        SERVICE_MAP.put(serviceName, service);
        log.info("add service [{}] and interfaces [{}]", serviceName, service.getClass().getInterfaces());
    }

    @Override
    public Object getService(RpcServiceProperties properties) {
        String serviceName = properties.toRpcServiceName();
        Object obj = null;
        try {
            if (SERVICE_MAP.containsKey(serviceName))
                obj = SERVICE_MAP.get(serviceName);
            else
                throw new Exception();
        } catch (Exception e) {
            log.error("must add service, then get service");
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public void publishService(Object service, RpcServiceProperties properties) {
        try {
            Class<?> anInterface = service.getClass().getInterfaces()[0];
            properties.setServiceName(anInterface.getCanonicalName());
            String serviceName = properties.toRpcServiceName();
            addService(service, properties);
            // 将服务注册到 ZooKeeper 注册中心
            SERVICE_REGISTER.registerService(
                    serviceName,
                    new InetSocketAddress(
                            InetAddress.getLocalHost().getHostAddress(),
                            NettyRpcServer.port));
        } catch (UnknownHostException e) {
            log.error("unknown host");
            e.printStackTrace();
        }
    }

    @Override
    public void publishService(Object service) {
        publishService(service, RpcServiceProperties.builder()
                .serviceName("")
                .version("")
                .group("")
                .build());
    }
}
