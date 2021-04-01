package cn.hznu.rpc;

import cn.hznu.rpc.proto.POJOOuter;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.UUID;

public class SerializationTest {
    public static void main(String[] args) throws InvalidProtocolBufferException {
        // 创建对象
        POJOOuter.POJO hello = POJOOuter.POJO.newBuilder()
                .setId(UUID.randomUUID().getLeastSignificantBits())
                .setName("hello")
                .setFlag(true).build();

        // 序列化
        byte[] bytes = hello.toByteArray();
        // 反序列化
        POJOOuter.POJO pojo = POJOOuter.POJO.parseFrom(bytes);

        System.out.println(pojo.getFlag());
    }
}
