package cn.qh.loadbalance;

import cn.qh.extension.SPI;

import java.util.List;

@SPI
public interface LoadBalance {
    String selectServiceAddress(String rpcServiceName, List<String> addressList);
}
