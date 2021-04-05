package cn.qh.loadbalance;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public String selectServiceAddress(String rpcServiceName, List<String> addressList) {
        if (addressList == null || addressList.size() == 0)
            return null;
        if (addressList.size() == 1)
            return addressList.get(0);
        return doSelect(rpcServiceName, addressList);
    }

    protected abstract String doSelect(String rpcServiceName, List<String> addressList);
}
