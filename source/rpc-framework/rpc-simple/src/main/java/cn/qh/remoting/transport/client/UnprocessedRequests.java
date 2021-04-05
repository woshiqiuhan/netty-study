package cn.qh.remoting.transport.client;


import cn.qh.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

// 存储响应的 future
public class UnprocessedRequests {
    private final static Map<String, CompletableFuture<RpcResponse<Object>>>
            UNPROCESSED_REQUESTS_MAP = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> completableFuture) {
        UNPROCESSED_REQUESTS_MAP.put(requestId, completableFuture);
    }

    public void complete(RpcResponse<Object> rpcResponse) {
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_REQUESTS_MAP.remove(rpcResponse.getRequestId());
        if (future != null) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }
}
