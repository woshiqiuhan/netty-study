package cn.qh.remoting.dto;

import cn.qh.entity.RpcServiceProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 4863805751335427757L;
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] args;
    private Class<?>[] argsType;
    private String version;
    private String group;

    public RpcServiceProperties toRpcServiceProperties() {
        return RpcServiceProperties.builder()
                .serviceName(getInterfaceName())
                .version(getVersion())
                .group(getGroup())
                .build();
    }
}
