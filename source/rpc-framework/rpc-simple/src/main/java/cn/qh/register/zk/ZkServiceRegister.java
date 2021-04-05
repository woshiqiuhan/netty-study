package cn.qh.register.zk;

import cn.qh.register.ServiceRegister;
import cn.qh.register.zk.util.CuratorUtil;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

public class ZkServiceRegister implements ServiceRegister {
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        rpcServiceName = rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtil.getZkClient();
        CuratorUtil.createPersistentNode(zkClient, rpcServiceName);
    }
}
