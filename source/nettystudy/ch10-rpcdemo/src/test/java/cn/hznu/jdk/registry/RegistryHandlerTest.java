package cn.hznu.jdk.registry;

import org.junit.Test;

public class RegistryHandlerTest {

    @Test
    public void testScannerClass() {
        System.out.println(RegistryHandler.registryMap);

        RegistryHandler.classNames.forEach(System.out::println);

        System.out.println(RegistryHandler.registryMap);
    }

}