package cn.bio.tomcat.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GPRequest {
    private String method;
    private String url;

    public GPRequest(InputStream in) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String s = reader.readLine();
            String[] strs = s.split(" ");
            this.method = strs[0];
            this.url = strs[1].split("\\?")[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }
}
