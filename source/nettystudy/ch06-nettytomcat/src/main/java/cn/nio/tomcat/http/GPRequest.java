package cn.nio.tomcat.http;

public class GPRequest {
    private String method;
    private String url;

    public GPRequest(String line) {
        String[] strs = line.split(" ");
        this.method = strs[0];
        this.url = strs[1].split("\\?")[0];
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }
}
