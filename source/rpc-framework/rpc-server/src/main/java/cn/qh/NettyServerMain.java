package cn.qh;

import cn.qh.annotation.RpcScan;
import cn.qh.config.CustomShutdownHook;
import cn.qh.remoting.transport.server.NettyRpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RpcScan(basePackage = "cn.qh.service")
public class NettyServerMain {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer server = new NettyRpcServer();
        server.start();
    }
}
