package cn.qiu.provider;

import cn.qiu.api.IRpcHelloService;

import java.util.HashMap;
import java.util.Map;

/**
 * 对 api包下类，即对外提供方法对实现
 */
public class IRpcHelloServiceImpl implements IRpcHelloService {
    @Override
    public String hello(String name) {
        return "Hello " + name + "!";
    }

    @Override
    public Map<String, Integer> getMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("Hellp", 222);
        return map;
    }
}
