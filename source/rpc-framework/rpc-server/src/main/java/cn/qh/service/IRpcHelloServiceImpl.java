package cn.qh.service;

import cn.qh.annotation.RpcService;

@RpcService(version = "1.0", group = "1.0")
public class IRpcHelloServiceImpl implements IRpcHelloService {
    @Override
    public String hello(String name) {
        return "你好！" + name;
    }
}
