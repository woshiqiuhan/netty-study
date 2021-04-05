package cn.qh.register;

import cn.qh.extension.SPI;

import java.net.InetSocketAddress;

// 注册服务
@SPI
public interface ServiceRegister {
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
