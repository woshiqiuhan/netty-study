package cn.qh.controller;

import cn.qh.annotation.RpcReference;
import cn.qh.service.IRpcHelloService;
import org.springframework.stereotype.Component;

@Component
public class IRpcHelloServiceController {

    @RpcReference(version = "1.0", group = "1.0")
    private IRpcHelloService iRpcHelloService;

    public String hello() {
        return iRpcHelloService.hello("秋寒");
    }
}