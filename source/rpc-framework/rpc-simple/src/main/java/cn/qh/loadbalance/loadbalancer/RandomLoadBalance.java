package cn.qh.loadbalance.loadbalancer;

import cn.qh.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

// ιζΊθ·ε
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(String rpcServiceName, List<String> addressList) {
        return addressList.get(new Random().nextInt(addressList.size()));
    }
}
