package cn.qh.spring;

import cn.qh.annotation.RpcReference;
import cn.qh.annotation.RpcService;
import cn.qh.entity.RpcServiceProperties;
import cn.qh.extension.ExtensionLoader;
import cn.qh.factory.SingletonFactory;
import cn.qh.provider.ServiceProvider;
import cn.qh.provider.ServiceProviderImpl;
import cn.qh.proxy.RpcClientProxy;
import cn.qh.remoting.transport.RpcRequestTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@SuppressWarnings("all")
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {
    private final ServiceProvider serviceProvider;
    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension("nettyClient");
    }

    // 为所有带有 RpcService 注解的类创建 bean 实例，即实例化所有服务
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            RpcService annotation = bean.getClass().getAnnotation(RpcService.class);
            RpcServiceProperties serviceProperties = RpcServiceProperties.builder()
                    .version(annotation.version())
                    .group(annotation.group())
                    .build();
            serviceProvider.publishService(bean, serviceProperties);
        }
        return bean;
    }

    // 实现 RpcReference 依赖注入
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            RpcReference fieldAnnotation = field.getAnnotation(RpcReference.class);
            if (fieldAnnotation == null)
                continue;
            RpcServiceProperties serviceProperties = RpcServiceProperties.builder()
                    .version(fieldAnnotation.version())
                    .group(fieldAnnotation.group())
                    .build();
            // 注入反射对象
            RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, serviceProperties);
            Object proxy = rpcClientProxy.getProxy(field.getType());
            field.setAccessible(true);
            try {
                field.set(bean, proxy);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return bean;
    }
}
