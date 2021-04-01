package cn.hznu.kryo;


import cn.hznu.kryo.utils.KryoSerializationUtils;
import cn.hznu.pojo.POJO;

import java.io.IOException;
import java.util.UUID;

public class SerializationTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        POJO pojo = new POJO(
                UUID.randomUUID().getLeastSignificantBits(),
                "Hello", false, 2);

        byte[] serialize = KryoSerializationUtils.serialize(pojo);
        System.out.println(serialize.length);

        pojo = KryoSerializationUtils.deserialize(serialize, POJO.class);
        System.out.println(pojo);
    }
}
