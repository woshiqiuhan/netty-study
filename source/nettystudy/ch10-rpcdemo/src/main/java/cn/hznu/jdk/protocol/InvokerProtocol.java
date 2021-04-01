package cn.hznu.jdk.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * 自定义协议
 * 远程调用方法是需要传递到参数
 */
@Data
public class InvokerProtocol implements Serializable {
    private static final long serialVersionUID = 5342946702102472150L;
    // 类名
    private String className;
    // 方法名
    private String methodName;
    // 参数类型
    private Class<?>[] params;
    // 参数列表
    private Object[] values;
}
