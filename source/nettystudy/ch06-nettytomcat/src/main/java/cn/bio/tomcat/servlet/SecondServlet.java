package cn.bio.tomcat.servlet;

import cn.bio.tomcat.http.GPRequest;
import cn.bio.tomcat.http.GPResponse;
import cn.bio.tomcat.http.GPServlet;

import java.io.IOException;

public class SecondServlet extends GPServlet {

    @Override
    protected void doPost(GPRequest request, GPResponse response) throws IOException {
        this.doGet(request, response);
    }

    @Override
    protected void doGet(GPRequest request, GPResponse response) throws IOException {
        response.write("<h1>This is SecondServlet!</h1>");
    }
}
