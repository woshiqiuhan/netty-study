package cn.qh.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CompressTypeEnum {
    GZIP((byte) 1, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressTypeEnum value : CompressTypeEnum.values())
            if (value.code == code)
                return value.name;
        return null;
    }
}
