package cn.qh.loadbalance.loadbalancer;

import cn.qh.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

// 随机获取
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(String rpcServiceName, List<String> addressList) {
        return addressList.get(new Random().nextInt(addressList.size()));
    }
}
