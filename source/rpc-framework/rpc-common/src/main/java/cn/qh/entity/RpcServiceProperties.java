package cn.qh.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RpcServiceProperties {
    private String serviceName;
    private String version;
    private String group;

    public String toRpcServiceName() {
        return serviceName + version + group;
    }
}
