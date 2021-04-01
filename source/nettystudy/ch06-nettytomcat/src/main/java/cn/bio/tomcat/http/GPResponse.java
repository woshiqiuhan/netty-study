package cn.bio.tomcat.http;

import java.io.IOException;
import java.io.OutputStream;

public class GPResponse {
    private final OutputStream out;

    public GPResponse(OutputStream out) {
        this.out = out;
    }

    public void write(String s) throws IOException {
        out.write(("HTTP/1.1 200 OK\n" +
                "Content-Type: text/html;\n" +
                "\r\n" +
                s).getBytes());
    }
}
