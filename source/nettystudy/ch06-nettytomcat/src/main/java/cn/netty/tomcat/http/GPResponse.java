package cn.netty.tomcat.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.StandardCharsets;

public class GPResponse {
    private final ChannelHandlerContext ctx;

    public GPResponse(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void write(String s) {
        try {
            if (s == null || s.length() == 0) {
                return;
            }
            // 设置 HTTP 请求头信息
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(s.getBytes(StandardCharsets.UTF_8)));

            response.headers().set("Content-Type", "text/html;");
            ctx.writeAndFlush(response);
        } finally {
            ctx.flush();
            ctx.close();
        }
    }
}
