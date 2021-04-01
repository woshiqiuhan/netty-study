package cn.hznu.hessian;

import cn.hznu.pojo.POJO;
import cn.hznu.hessian.utils.HessianSerializationUtils;

import java.io.IOException;
import java.util.UUID;

public class SerializationTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        POJO pojo = new POJO(
                UUID.randomUUID().getLeastSignificantBits(),
                "Hello", false, 2);
        // 序列化
        byte[] bytes = HessianSerializationUtils.serialize(pojo);
        System.out.println(bytes.length);
        // 反序列化
        POJO deserialize = (POJO) HessianSerializationUtils.deserialize(bytes);
    }
}