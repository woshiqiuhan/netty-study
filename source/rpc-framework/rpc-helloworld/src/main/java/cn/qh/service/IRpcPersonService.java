package cn.qh.service;

import cn.qh.pojo.Person;

public interface IRpcPersonService {
    Person getPersonBySNo(long sNo);

    Person getPersonBySName(String sName);
}
