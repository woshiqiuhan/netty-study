package cn.qh.remoting.dto;

import cn.qh.enums.RpcResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RpcResponse<T> implements Serializable {
    private static final long serialVersionUID = 5915970128848870748L;
    private String requestId;
    private Integer code;
    private String message;
    private T data;

    public static <T> RpcResponse<T> success(T data, String responseId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setMessage(RpcResponseCode.SUCCESS.getMessage());
        response.setCode(RpcResponseCode.SUCCESS.getCode());
        response.setRequestId(responseId);
        if (data != null)
            response.setData(data);
        return response;
    }

    public static <T> RpcResponse<T> fail(RpcResponseCode rpcResponseCode) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setMessage(rpcResponseCode.getMessage());
        response.setCode(rpcResponseCode.getCode());
        return response;
    }
}
