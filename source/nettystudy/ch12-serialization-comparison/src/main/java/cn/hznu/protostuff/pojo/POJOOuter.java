package cn.hznu.protostuff.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class POJOOuter {
    private POJO pojo;
}
