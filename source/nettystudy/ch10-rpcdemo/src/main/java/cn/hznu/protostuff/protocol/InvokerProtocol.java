package cn.hznu.protostuff.protocol;

import lombok.Builder;
import lombok.Data;

/**
 * 自定义协议
 * 远程调用方法是需要传递到参数
 */
@Builder
@Data
public class InvokerProtocol {
    private String className;
    private String methodName;
    private Class<?>[] params;
    private Object[] values;
}
