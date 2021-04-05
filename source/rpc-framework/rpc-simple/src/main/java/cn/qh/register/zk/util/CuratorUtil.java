package cn.qh.register.zk.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CuratorUtil {

    private final static String DEFAULT_ZK_ADDRESS = "127.0.0.1:2181";
    private static CuratorFramework zkClient;
    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    private static final Set<String> REGISTER_PATH_SET = ConcurrentHashMap.newKeySet();
    public static String ZK_ROOT_PATH = "/qh-rpc";

    public static void createPersistentNode(CuratorFramework zkClient, String rpcServiceName) {
        String path = ZK_ROOT_PATH + "/" + rpcServiceName;
        try {
            if (REGISTER_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null)
                log.info("the node already exists");
            else {
                log.info("create node successfully");
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
            }
            REGISTER_PATH_SET.add(path);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("create node failed");
        }
    }

    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        List<String> res = null;
        String path = ZK_ROOT_PATH + "/" + rpcServiceName;
        try {
            res = zkClient.getChildren().forPath(path);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, res);
            registerWatcher(zkClient, rpcServiceName);
        } catch (Exception e) {
            log.error("get children nodes failed");
        }
        return res;
    }

    public static CuratorFramework getZkClient() {
        // 通过配置文件获取zk的地址
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED)
            return zkClient;
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(DEFAULT_ZK_ADDRESS)
                .retryPolicy(retry)
                .build();
        zkClient.start();
        try {
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS))
                throw new RuntimeException("Time out waiting to connect to zk");
        } catch (RuntimeException | InterruptedException e) {
            log.error("get client failed");
        }
        return zkClient;
    }

    private static void registerWatcher(CuratorFramework zkClient, String rpcServiceName) {
        String path = ZK_ROOT_PATH + "/" + rpcServiceName;
        try {
            PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, path, true);
            pathChildrenCache.getListenable()
                    .addListener((curatorFramework, pathChildrenCacheEvent) -> {
                        List<String> res = curatorFramework.getChildren().forPath(path);
                        SERVICE_ADDRESS_MAP.put(rpcServiceName, res);
                    });
            pathChildrenCache.start();
        } catch (Exception e) {
            log.error("register watcher failed");
        }
    }

    public static void clearRegister(CuratorFramework zkClient, InetSocketAddress inetAddress) {
        REGISTER_PATH_SET.stream().parallel().forEach(p -> {
            if (p.endsWith(inetAddress.toString())) {
                try {
                    zkClient.delete().forPath(p);
                } catch (Exception e) {
                    log.error("clear services failed");
                }
            }
        });
        log.info("clear all services");
    }
}