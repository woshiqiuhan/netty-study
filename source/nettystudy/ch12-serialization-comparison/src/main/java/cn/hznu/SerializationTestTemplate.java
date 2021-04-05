package cn.hznu;

import java.io.IOException;

public class SerializationTestTemplate {
    public static void test(SerializationUtils serializationUtils, Object pojo) throws IOException, ClassNotFoundException {
        System.out.println(serializationUtils.getClass().getName() + " 测试结果：");
        byte[] bytes = serializationUtils.serialize(pojo);
        // 序列化后字节数
        System.out.println("bytes size: " + bytes.length);
        long a = 0, b = 0;
        int n = 1000000;
        while (n-- != 0) {
            long start = System.currentTimeMillis();
            // 序列化
            bytes = serializationUtils.serialize(pojo);
            a += System.currentTimeMillis() - start;
            start = System.currentTimeMillis();
            // 反序列化
            serializationUtils.deserialize(bytes);
            b += System.currentTimeMillis() - start;
        }
        System.out.println("serialize: " + a + " ms");
        System.out.println("deserialize: " + b + " ms");
        System.out.println();
    }
}