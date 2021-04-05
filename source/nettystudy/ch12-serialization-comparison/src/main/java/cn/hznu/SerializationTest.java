package cn.hznu;

import cn.hznu.hessian.HessianSerializationUtils;
import cn.hznu.jdk.JDKSerializationUtils;
import cn.hznu.kryo.KryoSerializationUtils;
import cn.hznu.pojo.POJO;
import cn.hznu.protobuf.ProtobufSerializationUtils;
import cn.hznu.protobuf.proto.POJOOuter;
import cn.hznu.protostuff.ProtostuffSerializationUtils;

import java.io.IOException;
import java.util.UUID;

public class SerializationTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        POJO pojo = new POJO(
                UUID.randomUUID().getLeastSignificantBits(),
                "Hello", false, 2);
        // JDK 原生序列化
        SerializationTestTemplate.test(new JDKSerializationUtils(), pojo);

        // Hessian2 序列化
        SerializationTestTemplate.test(new HessianSerializationUtils(), pojo);

        // Kryo 序列化
        SerializationTestTemplate.test(new KryoSerializationUtils(), pojo);

        // Protobuf
        POJOOuter.POJO pojoo = POJOOuter.POJO.newBuilder()
                .setId(UUID.randomUUID().getLeastSignificantBits())
                .setName("hello")
                .setFlag(true).build();
        SerializationTestTemplate.test(new ProtobufSerializationUtils(), pojoo);

        // Protostuff 序列化
        SerializationTestTemplate.test(new ProtostuffSerializationUtils(), pojo);
    }
}
