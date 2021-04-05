package cn.qh.remoting.dto;

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
public class RpcMessage {
    private byte messageType;
    private byte coderType;
    private byte compressType;
    private int requestId;
    private Object data;
}
