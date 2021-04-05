package cn.qh.config;

import cn.qh.register.zk.util.CuratorUtil;
import cn.qh.remoting.transport.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

// 关闭所有服务端(对应地址)在 ZooKeeper 中注册的服务
@Slf4j
public class CustomShutdownHook {
    private final static CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyRpcServer.port);
                CuratorUtil.clearRegister(CuratorUtil.getZkClient(), address);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }));
    }
}

