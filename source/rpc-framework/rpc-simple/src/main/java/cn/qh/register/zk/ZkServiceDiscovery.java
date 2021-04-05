package cn.qh.register.zk;

import cn.qh.extension.ExtensionLoader;
import cn.qh.loadbalance.LoadBalance;
import cn.qh.register.ServiceDiscovery;
import cn.qh.register.zk.util.CuratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("consistentHashLoadBalance");
    }

    @Override
    public InetSocketAddress lookupService(String rpcServiceName) {
        CuratorFramework zkClient = CuratorUtil.getZkClient();
        InetSocketAddress inetSocketAddress = null;
        try {
            List<String> addressList = CuratorUtil.getChildrenNodes(zkClient, rpcServiceName);
            if (addressList == null || addressList.size() == 0) {
                throw new Exception();
            }

            String address = loadBalance.selectServiceAddress(rpcServiceName, addressList);
            String[] tmpValue = address.split(":");
            inetSocketAddress = new InetSocketAddress(tmpValue[0], Integer.parseInt(tmpValue[1]));
            log.info("look up service successfully : [{}]", address);
        } catch (Exception e) {
            log.error("look up service failed");
            e.printStackTrace();
        }
        return inetSocketAddress;
    }
}