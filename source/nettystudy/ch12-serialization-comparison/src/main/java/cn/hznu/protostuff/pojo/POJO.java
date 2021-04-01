package cn.hznu.protostuff.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class POJO {
    private Long id;
    private String name;
    private Boolean flag;
}
