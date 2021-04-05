package cn.qh;

import cn.qh.annotation.RpcScan;
import cn.qh.controller.IRpcCalculationServiceController;
import cn.qh.controller.IRpcHelloServiceController;
import cn.qh.controller.IRpcPersonServiceController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.UUID;

@RpcScan(basePackage = "cn.qh.controller")
public class NettyClientMain {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(NettyClientMain.class);

        IRpcHelloServiceController helloWorldController = (IRpcHelloServiceController) context.getBean("IRpcHelloServiceController");
        System.out.println(helloWorldController.hello());

        IRpcCalculationServiceController iRpcCalculationServiceController = (IRpcCalculationServiceController) context.getBean("IRpcCalculationServiceController");
        System.out.println(iRpcCalculationServiceController.add(1, 3));
        System.out.println(iRpcCalculationServiceController.sub(1, 3));
        System.out.println(iRpcCalculationServiceController.multi(1, 3));
        System.out.println(iRpcCalculationServiceController.div(6, 3));

        IRpcPersonServiceController iRpcPersonServiceController = (IRpcPersonServiceController) context.getBean("IRpcPersonServiceController");
        System.out.println(iRpcPersonServiceController.getPersonBySName("秋寒"));
        System.out.println(iRpcPersonServiceController.getPersonBySNo(Math.abs(UUID.randomUUID().getLeastSignificantBits())));
    }
}