package cn.netty.tomcat.servlet;

import cn.netty.tomcat.http.GPRequest;
import cn.netty.tomcat.http.GPResponse;
import cn.netty.tomcat.http.GPServlet;

import java.io.IOException;

public class FirstServlet extends GPServlet {

    @Override
    protected void doPost(GPRequest request, GPResponse response) throws IOException {
        this.doGet(request, response);
    }

    @Override
    protected void doGet(GPRequest request, GPResponse response) throws IOException {
        response.write("<h1>This is FirstServlet!</h1>");
    }
}
