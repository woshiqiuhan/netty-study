package cn.hznu.protostuff;

import cn.hznu.SerializationUtils;
import cn.hznu.pojo.POJO;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.IOException;

public class ProtostuffSerializationUtils implements SerializationUtils {
    private static final LinkedBuffer BUFFER =
            LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    @SuppressWarnings("all")
    @Override
    public byte[] serialize(Object obj) {
        Class<?> clazz = obj.getClass();
        Schema schema = RuntimeSchema.getSchema(clazz);
        byte[] bytes;
        try {
            bytes = ProtostuffIOUtil.toByteArray(obj, schema, BUFFER);
        } finally {
            BUFFER.clear();
        }
        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        return deserialize(bytes, POJO.class);
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }
}
