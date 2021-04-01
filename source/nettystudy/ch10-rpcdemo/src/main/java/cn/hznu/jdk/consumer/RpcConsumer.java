package cn.hznu.jdk.consumer;

import cn.hznu.jdk.api.IRpcHelloService;
import cn.hznu.jdk.api.IRpcService;
import cn.hznu.jdk.consumer.proxy.RpcProxy;

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
