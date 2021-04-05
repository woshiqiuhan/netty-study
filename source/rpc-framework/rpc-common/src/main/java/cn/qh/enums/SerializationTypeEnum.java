package cn.qh.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerializationTypeEnum {
    KRYO((byte) 1, "kryo"),
    PROTOSTUFF((byte) 2, "protostuff");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (SerializationTypeEnum value : SerializationTypeEnum.values())
            if (value.code == code)
                return value.name;
        return null;
    }
}
