package cn.qh.annotation;

import cn.qh.spring.RpcScannerRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 扫面包并将包下的组件添加进容器中
@Target({ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Import(RpcScannerRegister.class)
public @interface RpcScan {
    String[] basePackage();
}
