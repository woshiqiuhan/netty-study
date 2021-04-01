package cn.netty.tomcat.http;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

public class GPRequest {
    private final HttpRequest request;

    public GPRequest(HttpRequest request) {
        this.request = request;
    }

    public String getMethod() {
        return request.method().name();
    }

    public String getUrl() {
        return request.uri();
    }

    public Map<String, List<String>> getParameters() {
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        return decoder.parameters();
    }

    public String getParameter(String name) {
        Map<String, List<String>> parameters = getParameters();
        List<String> parameter = parameters.get(name);
        if (null == parameter) {
            return null;
        } else {
            return parameter.get(0);
        }
    }
}
