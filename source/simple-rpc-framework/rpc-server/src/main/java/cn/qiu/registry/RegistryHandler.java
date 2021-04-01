package cn.qiu.registry;

import cn.qiu.protocol.RpcMessage;
import cn.qiu.protocol.RpcRequest;
import cn.qiu.protocol.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class RegistryHandler extends ChannelInboundHandlerAdapter {
    // 保存所有可用服务
    public static ConcurrentHashMap<String, Object> registryMap = new ConcurrentHashMap<>();

    // 保存所有相关服务类
    public static List<String> classNames = new ArrayList<>();

    static {
        scannerClass("cn.qiu.provider");
        doRegister();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object result = new Object();

        RpcRequest request = (RpcRequest) ((RpcMessage) msg).getRpcMessage();
        System.out.println(msg);
        if (registryMap.containsKey(request.getClassName())) {
            Object aClass = registryMap.get(request.getClassName());

            Method method = aClass.getClass()
                    .getMethod(request.getMethodName(), request.getParams());
            result = method.invoke(aClass, request.getValues());
        }
        ctx.writeAndFlush(RpcMessage.builder().rpcMessage(RpcResponse.builder().rpcResponse(result).build()).build());
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    // 递归扫描包下所有类
    private static void scannerClass(String packageName) {
        // 获取资源定位符
        URL url = RegistryHandler.class.getClassLoader().
                getResource(packageName.replaceAll("\\.", "/"));

        if (url == null) {
            return;
        }

        File dir = new File(url.getFile());
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                scannerClass(packageName + "." + file.getName());
            } else {
                classNames.add(packageName + "." +
                        file.getName().replace(".class", "").trim());
            }
        }
    }

    // 注册服务
    private static void doRegister() {
        classNames.forEach(className -> {
            try {
                // 获取服务对应的 class
                Class<?> aClass = Class.forName(className);
                // 找到对应实现接口，即要暴露的服务
                Class<?> anInterface = aClass.getInterfaces()[0];
                // 形成映射 api名字 = provider对象
                registryMap.put(anInterface.getName(), aClass.newInstance());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        });
    }
}
