package cn.qh.service;

import cn.qh.annotation.RpcService;
import cn.qh.pojo.Person;

import java.util.UUID;

@RpcService(version = "1.0", group = "1.0")
public class IRpcPersonServiceImpl implements IRpcPersonService {
    @Override
    public Person getPersonBySNo(long sNo) {
        return Person.builder()
                .sNo(sNo)
                .sName("秋寒")
                .sAge(22)
                .sSex(true).build();
    }

    @Override
    public Person getPersonBySName(String sName) {
        return new Person(
                Math.abs(UUID.randomUUID().getLeastSignificantBits()),
                sName, true, 22);
    }
}
