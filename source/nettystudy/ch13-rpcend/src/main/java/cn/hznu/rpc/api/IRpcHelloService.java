package cn.hznu.rpc.api;

import java.util.Map;

public interface IRpcHelloService {
    String hello(String name);

    Map<String, Integer> getMap();
}
