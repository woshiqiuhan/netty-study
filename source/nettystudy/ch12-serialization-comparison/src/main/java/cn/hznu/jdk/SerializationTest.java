package cn.hznu.jdk;

import cn.hznu.jdk.utils.JDKSerializationUtils;
import cn.hznu.pojo.POJO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

public class SerializationTest {
    /*public static void main(String[] args) throws IOException, ClassNotFoundException {
        POJO pojo = new POJO(
                UUID.randomUUID().getLeastSignificantBits(),
                "Hello", false, 2);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        // 序列化
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            oos.writeObject(pojo);
        }
        System.out.println("序列化用时：" + (System.currentTimeMillis() - start) + "ms");


        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        // 反序列化
        start = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            pojo = (POJO) ois.readObject();
        }
        System.out.println("反序列化用时：" + (System.currentTimeMillis() - start) + "ms");

        System.out.println(pojo);
    }*/

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        POJO pojo = new POJO(
                UUID.randomUUID().getLeastSignificantBits(),
                "Hello", false, 2);

        byte[] serialize = JDKSerializationUtils.serialize(pojo);
        System.out.println(serialize.length);

        pojo = (POJO) JDKSerializationUtils.deserialize(serialize);
        System.out.println(pojo);
    }
}
