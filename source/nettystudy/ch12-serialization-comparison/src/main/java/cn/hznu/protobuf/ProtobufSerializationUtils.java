package cn.hznu.protobuf;

import cn.hznu.SerializationUtils;
import cn.hznu.protobuf.proto.POJOOuter;

import java.io.IOException;

public class ProtobufSerializationUtils implements SerializationUtils {
    @Override
    public byte[] serialize(Object obj) {
        return ((POJOOuter.POJO) obj).toByteArray();
    }

    @Override
    public Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        return POJOOuter.POJO.parseFrom(bytes);
    }
}
