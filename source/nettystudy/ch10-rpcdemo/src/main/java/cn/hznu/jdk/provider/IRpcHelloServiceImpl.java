package cn.hznu.jdk.provider;

import cn.hznu.jdk.api.IRpcHelloService;

/**
 * 对 api包下类，即对外提供方法对实现
 */
public class IRpcHelloServiceImpl implements IRpcHelloService {
    @Override
    public String hello(String name) {
        return "Hello " + name + "!";
    }
}
