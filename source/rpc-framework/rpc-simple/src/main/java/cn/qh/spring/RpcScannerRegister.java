package cn.qh.spring;

import cn.qh.annotation.RpcScan;
import cn.qh.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

@SuppressWarnings("all")
@Slf4j
public class RpcScannerRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private final static String BASE_PACKAGE = "basePackage";
    private final static String SPRING_COMPONENT_PACKAGE = "cn.qh.spring";

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(RpcScan.class.getName()));
        String[] rpcScannerBasePackage = null;
        if (annotationAttributes != null) {
            rpcScannerBasePackage = annotationAttributes.getStringArray(BASE_PACKAGE);
        }
        // 扫描
        CustomScanner rpcServiceScanner = new CustomScanner(registry, RpcService.class);
        CustomScanner springScanner = new CustomScanner(registry, Component.class);
        if (resourceLoader != null) {
            rpcServiceScanner.setResourceLoader(resourceLoader);
            springScanner.setResourceLoader(resourceLoader);
        }

        log.info("The number of Spring scans : [{}]", springScanner.scan(SPRING_COMPONENT_PACKAGE));

        log.info("The number of RPC Server scans : [{}]", rpcServiceScanner.scan(rpcScannerBasePackage));
    }
}
