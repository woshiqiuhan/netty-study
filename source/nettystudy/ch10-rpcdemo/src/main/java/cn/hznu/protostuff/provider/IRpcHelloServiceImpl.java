package cn.hznu.protostuff.provider;

import cn.hznu.protobuf.api.IRpcHelloService;

/**
 * 对 api包下类，即对外提供方法对实现
 */
public class IRpcHelloServiceImpl implements IRpcHelloService {
    @Override
    public String hello(String name) {
        return "Hello " + name + "!";
    }
}
