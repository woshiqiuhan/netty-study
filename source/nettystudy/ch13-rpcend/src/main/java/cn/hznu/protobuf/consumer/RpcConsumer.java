package cn.hznu.protobuf.consumer;

import cn.hznu.protobuf.api.IRpcHelloService;
import cn.hznu.protobuf.api.IRpcService;
import cn.hznu.protobuf.consumer.proxy.RpcProxy;

import java.util.Map;

public class RpcConsumer {
    public static void main(String[] args) {
        IRpcHelloService rpcHello = RpcProxy.create(IRpcHelloService.class);

        System.out.println(rpcHello.hello("Tom"));
        Map<String, Integer> map = rpcHello.getMap();
        System.out.println(map);

        IRpcService rpcService = RpcProxy.create(IRpcService.class);

        System.out.println(rpcService.add(1, 2));
        System.out.println(rpcService.sub(1, 2));
        System.out.println(rpcService.mult(1, 2));
        System.out.println(rpcService.div(6, 2));
    }
}
