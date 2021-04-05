package cn.qh.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

public class CustomScanner extends ClassPathBeanDefinitionScanner {

    @SafeVarargs
    public CustomScanner(BeanDefinitionRegistry beanDefinitionRegistry, Class<? extends Annotation>... annotationTypes) {
        super(beanDefinitionRegistry);
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            super.addIncludeFilter(new AnnotationTypeFilter(annotationType));
        }
    }

    public CustomScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }
}
