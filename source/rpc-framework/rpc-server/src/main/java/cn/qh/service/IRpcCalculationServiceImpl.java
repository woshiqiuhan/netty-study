package cn.qh.service;

import cn.qh.annotation.RpcService;

@RpcService(version = "1.0", group = "1.0")
public class IRpcCalculationServiceImpl implements IRpcCalculationService {
    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int sub(int a, int b) {
        return a - b;
    }

    @Override
    public int multi(int a, int b) {
        return a * b;
    }

    @Override
    public int div(int a, int b) {
        return a / b;
    }
}
