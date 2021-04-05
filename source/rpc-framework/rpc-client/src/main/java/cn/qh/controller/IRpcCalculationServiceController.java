package cn.qh.controller;

import cn.qh.annotation.RpcReference;
import cn.qh.service.IRpcCalculationService;
import org.springframework.stereotype.Component;

@Component
public class IRpcCalculationServiceController {
    @RpcReference(version = "1.0", group = "1.0")
    private IRpcCalculationService iRpcCalculationService;

    public int add(int a, int b) {
        return iRpcCalculationService.add(a, b);
    }

    public int sub(int a, int b) {
        return iRpcCalculationService.sub(a, b);
    }

    public int multi(int a, int b) {
        return iRpcCalculationService.multi(a, b);
    }

    public int div(int a, int b) {
        return iRpcCalculationService.div(a, b);
    }
}
