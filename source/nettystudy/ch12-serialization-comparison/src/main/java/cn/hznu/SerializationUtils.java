package cn.hznu;

import java.io.IOException;

public interface SerializationUtils {
    // 序列化
    byte[] serialize(Object o) throws IOException;

    // 反序列化
    Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException;
}
