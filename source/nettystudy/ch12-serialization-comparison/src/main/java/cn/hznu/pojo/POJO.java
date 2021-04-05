package cn.hznu.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class POJO implements Serializable { // 实现 Serializable 接口，表示当前类可被序列化
    /**
     * 设置 serialVersionUID 序列化ID的作用
     *     序列化操作时会将当前类声明的 serialVersionUID 写入到序列化文件中，
     *  反序列化时会对比文件中的 serialVersionUID 与类的 serialVersionUID 是否相同，
     *  如果相同则序列化成功，否则失败，该操作用于保证版本一致。
     * 注：若未显示声明 serialVersionUID，序列化时会根据类生成一个存入文件中，
     *    若类被修改过则 serialVersionUID 会改变
     */
    private static final long serialVersionUID = -2640724836289593418L;

    private Long id;
    private String name;
    private Boolean flag;

    /**
     * 使用 transient 关键词修饰，代表该属性不需要序列化
     * 该对象在反序列化出来结果之后，相应的属性就会为null值
     */
    private transient Integer notSerializable;
}