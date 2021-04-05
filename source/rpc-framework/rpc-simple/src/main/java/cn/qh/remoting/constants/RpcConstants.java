package cn.qh.remoting.constants;

public class RpcConstants {
    // 报文类型
    public static final byte REQUEST_TYPE = 1;
    public static final byte RESPONSE_TYPE = 2;
    public static final byte HEART_REQUEST = 3;
    public static final byte HEART_RESPONSE = 4;

    // 心跳检测
    public static final String PING = "ping";
    public static final String PONG = "pong";

    // 报文最大长度
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

    // 魔数，用于校验报文是否该服务器接收
    public static final byte[] MAGIC_CODE = new byte[]{(byte) 'c', (byte) 'r', (byte) 'p', (byte) 'c'};

    public static final byte VERSION = 1;
    public static final int VERSION_LENGTH = 1;

    // 报文头长度
    public static final int HEAD_LENGTH = 16;
}
