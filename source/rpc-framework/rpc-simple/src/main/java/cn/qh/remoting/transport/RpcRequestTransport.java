package cn.qh.remoting.transport;

import cn.qh.extension.SPI;
import cn.qh.remoting.dto.RpcRequest;

@SPI
public interface RpcRequestTransport {
    Object sendRequest(RpcRequest rpcRequest);
}
