package cn.hznu.zerocopy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class OldClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 7931);

        OutputStream os = socket.getOutputStream();
        FileInputStream fis = new FileInputStream("/Users/qiuhan/Desktop/3.zip");
        int len = 0, size = 0;
        byte[] bytes = new byte[1024];
        long s = System.currentTimeMillis();
        while ((len = fis.read(bytes)) != -1) {
            os.write(bytes, 0, len);
            size += len;
        }

        System.out.println("传输完成，共传输" + size + "字节，用时" + (System.currentTimeMillis() - s) + "ms");
    }
}
