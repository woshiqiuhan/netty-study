package cn.hznu.protostuff;

import cn.hznu.protostuff.pojo.POJO;
import cn.hznu.protostuff.pojo.POJOOuter;
import cn.hznu.protostuff.utils.ProtostuffSerializationUtils;

import java.util.UUID;

public class SerializationTest {
    /*public static void main(String[] args) {
        POJO pojo = POJO.builder()
                .id(UUID.randomUUID().getLeastSignificantBits())
                .name("hello")
                .flag(true).build();

        POJOOuter pojoOuter = POJOOuter.builder()
                .pojo(pojo).build();

        byte[] serialize = ProtostuffSerializationUtils.serialize(pojoOuter);

        POJOOuter outer = ProtostuffSerializationUtils.
                deserialize(serialize, POJOOuter.class);
        System.out.println(outer);
    }*/

    public static void main(String[] args) {
        POJO pojo = POJO.builder()
                .id(UUID.randomUUID().getLeastSignificantBits())
                .name("hello")
                .flag(true).build();


        byte[] serialize = ProtostuffSerializationUtils.serialize(pojo);

        POJO outer = ProtostuffSerializationUtils.
                deserialize(serialize, POJO.class);
        System.out.println(outer);
    }
}
