package cn.qh.register;

import cn.qh.extension.SPI;

import java.net.InetSocketAddress;

// 寻找服务对象
@SPI
public interface ServiceDiscovery {
    InetSocketAddress lookupService(String rpcServiceName);
}
