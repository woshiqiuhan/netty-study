package cn.hznu.hessian.utils;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializationUtils {
    public static byte[] serialize(Object o) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Hessian2Output hos = new Hessian2Output(bos);
        hos.writeObject(o);
        hos.flush();
        byte[] bytes = bos.toByteArray();
        bos.close();
        hos.close();
        return bytes;
    }

    public static Object deserialize(byte[] bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Hessian2Input his = new Hessian2Input(bis);
        Object o = his.readObject();
        bis.close();
        his.close();
        return o;
    }
}
