package cn.qh.controller;

import cn.qh.annotation.RpcReference;
import cn.qh.pojo.Person;
import cn.qh.service.IRpcPersonService;
import org.springframework.stereotype.Component;

@Component
public class IRpcPersonServiceController {

    @RpcReference(version = "1.0", group = "1.0")
    private IRpcPersonService iRpcPersonService;

    public Person getPersonBySNo(long sNo) {
        return iRpcPersonService.getPersonBySNo(sNo);
    }

    public Person getPersonBySName(String sName) {
        return iRpcPersonService.getPersonBySName(sName);
    }
}
