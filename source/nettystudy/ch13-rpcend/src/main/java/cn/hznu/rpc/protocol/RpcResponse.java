package cn.hznu.rpc.protocol;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RpcResponse {
    private Object rpcResponse;
}
