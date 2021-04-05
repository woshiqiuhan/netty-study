package cn.qh.loadbalance.loadbalancer;

import cn.qh.loadbalance.AbstractLoadBalance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("all")
public class ConsistentHashLoadBalance extends AbstractLoadBalance {

    private final static ConcurrentMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();
    // 设置默认虚拟节点数
    private final static int DEFAULT_REPLICA_NUMBER = 160;

    @Override
    protected String doSelect(String rpcServiceName, List<String> addressList) {
        // 获取 service 原始的 hashcode
        int hashCode = addressList.hashCode();
        ConsistentHashSelector selector = selectors.get(rpcServiceName);

        // 如果 service 对应地址是一个新的 List 对象
        // 意味着服务提供者数量发生了变化，可能新增也可能减少了
        // 此时 selector.identityHashCode != hashCode 条件成立
        if (selector == null || selector.identityHashCode != hashCode) {
            // 创建新的 ConsistentHashSelector
            selectors.put(rpcServiceName, new ConsistentHashSelector(addressList, hashCode, DEFAULT_REPLICA_NUMBER));
            selector = selectors.get(rpcServiceName);
        }
        // 调用 ConsistentHashSelector 的 select 方法选择 service 地址
        return selector.select(rpcServiceName);
    }

    private static class ConsistentHashSelector {
        private final TreeMap<Long, String> virtualInvokers;
        private final long identityHashCode;

        public ConsistentHashSelector(List<String> addressList, long identityHashCode, int replicaNumber) {
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;
            for (String address : addressList) {
                for (int i = 0; i < replicaNumber / 4; i++) {
                    // 对 address + i 进行 md5 运算，得到一个长度为16的字节数组
                    byte[] bytes = md5(address + i);
                    for (int h = 0; h < 4; h++) {
                        // 对 digest 部分字节进行4次 hash 运算，得到四个不同的 long 型正整数
                        // h = 0 时，取 digest 中下标为 0 ~ 3 的4个字节进行位运算
                        // h = 1 时，取 digest 中下标为 4 ~ 7 的4个字节进行位运算
                        // h = 2, h = 3 时过程同上
                        long hashCode = hash(bytes, h);
                        // 将 hash 到 service 的映射关系存储到 virtualInvokers 中，
                        // virtualInvokers 需要提供高效的查询操作，因此选用 TreeMap 作为存储结构
                        virtualInvokers.put(hashCode, address);
                    }
                }
            }
        }

        private static byte[] md5(String key) {
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                md5.update(key.getBytes(StandardCharsets.UTF_8));
                return md5.digest();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return null;
        }

        private static long hash(byte[] bytes, int idx) {
            return (((long) (bytes[3 + idx * 4] & 0xFF) << 24)
                    | ((long) (bytes[2 + idx * 4] & 0xFF) << 16)
                    | ((long) (bytes[1 + idx * 4] & 0xFF) << 8)
                    | (bytes[idx * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }

        public String select(String rpcServiceName) {
            byte[] key = md5(rpcServiceName);
            return doSelectForKey(hash(key, 0));
        }

        private String doSelectForKey(long hashCode) {
            // 到 TreeMap 中查找第一个节点值大于或等于当前 hash 的 service
            Map.Entry<Long, String> entry = virtualInvokers.tailMap(hashCode, true).firstEntry();

            // 如果 hash 大于 service 在圆环上最大的位置，此时 entry = null，
            // 需要将 TreeMap 的头节点赋值给 entry
            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }
            return entry.getValue();
        }
    }
}