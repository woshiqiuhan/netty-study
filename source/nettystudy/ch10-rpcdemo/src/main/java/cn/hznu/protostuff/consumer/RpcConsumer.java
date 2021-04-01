package cn.hznu.protostuff.consumer;

import cn.hznu.rpc.api.IRpcHelloService;
import cn.hznu.rpc.api.IRpcService;
import cn.hznu.rpc.consumer.proxy.RpcProxy;

public class RpcConsumer {
    public static void main(String[] args) {
        IRpcHelloService rpcHello = RpcProxy.create(IRpcHelloService.class);

        System.out.println(rpcHello.hello("Tom"));

        IRpcService rpcService = RpcProxy.create(IRpcService.class);

        System.out.println(rpcService.add(1, 2));
        System.out.println(rpcService.sub(1, 2));
        System.out.println(rpcService.mult(1, 2));
        System.out.println(rpcService.div(6, 2));
    }
}
