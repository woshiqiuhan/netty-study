package cn.hznu.zerocopy;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class OldServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7931);
        byte[] bytes = new byte[1024];
        while (true) {
            Socket socket = serverSocket.accept();
            InputStream is = socket.getInputStream();
            int len = 0;
            while ((len = is.read(bytes)) != -1) ;
        }
    }
}
