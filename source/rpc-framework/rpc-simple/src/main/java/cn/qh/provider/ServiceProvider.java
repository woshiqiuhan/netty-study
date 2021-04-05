package cn.qh.provider;

import cn.qh.entity.RpcServiceProperties;

public interface ServiceProvider {
    // 添加 服务-服务映射 到映射表中
    void addService(Object service, RpcServiceProperties properties);

    // 获得具体服务对象
    Object getService(RpcServiceProperties properties);

    // 发布服务到 Zookeeper
    void publishService(Object service, RpcServiceProperties properties);

    void publishService(Object service);
}
