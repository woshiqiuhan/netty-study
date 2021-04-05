package cn.qh.remoting.transport.coder;


import cn.qh.compress.Compress;
import cn.qh.enums.CompressTypeEnum;
import cn.qh.enums.SerializationTypeEnum;
import cn.qh.extension.ExtensionLoader;
import cn.qh.remoting.constants.RpcConstants;
import cn.qh.remoting.dto.RpcMessage;
import cn.qh.remoting.dto.RpcRequest;
import cn.qh.remoting.dto.RpcResponse;
import cn.qh.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * 报文字段说明
 * <p>
 * 0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 * +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 * |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 * +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 * |                                                                                                       |
 * |                                         body                                                          |
 * |                                                                                                       |
 * |                                        ... ...                                                        |
 * +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）：
 * 即魔数，通常是 4 个字节。这个魔数主要是为了筛选来到服务端的数据包，有了这个魔数之后，
 * 服务端首先取出前面四个字节进行比对，能够在第一时间识别出这个数据包并非是遵循自定义协议的，
 * 也就是无效数据包，为了安全考虑可以直接关闭连接以节省资源。
 * 1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 */

@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    public RpcMessageDecoder() {
        // 根据上述报文格式，标识报文长度的字段处于 ByteBuf 的 5 - 9 之间
        // 具体剩余参数参照 https://zhuanlan.zhihu.com/p/95621344
        this(RpcConstants.MAX_FRAME_LENGTH,
                5, 4,
                -9, 0);
    }

    private RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset,
                lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // 调用父类的 decode，对报文进行解析
        // 将报文中的数据即 RpcMessage 的 ByteBuf 返回
        Object decode = super.decode(ctx, in);
        // 调用自定义 decoder 进行报文解析
        if (decode instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) decode;
            if (buf.readableBytes() >= RpcConstants.HEAD_LENGTH) {
                // 判断报文是否合理
                try {
                    return decodeFrame(buf);
                } catch (Exception e) {
                    log.error("decode error");
                    throw new Exception("decode error");
                } finally {
                    buf.release();
                }
            }
        }
        return decode;
    }

    private Object decodeFrame(ByteBuf in) {
        checkMagicCode(in);  // 校验魔数
        checkVersion(in);  // 校验版本
        int fullLength = in.readInt();
        byte messageType = in.readByte();
        byte coderType = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();
        // 恢复请求报文头
        RpcMessage rpcMessage = RpcMessage.builder()
                .messageType(messageType)
                .coderType(coderType)
                .compressType(compressType)
                .requestId(requestId)
                .build();
        // 判断是否为心跳检测请求报文
        if (messageType == RpcConstants.HEART_REQUEST) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        // 判断是否为心跳检测响应报文
        if (messageType == RpcConstants.HEART_RESPONSE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }

        // 读取请求报文体，即数据
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] body = new byte[bodyLength];
            in.readBytes(body);

            // 解压，gzip
            String compressName = CompressTypeEnum.getName(compressType);
            Compress compress =
                    ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
            body = compress.decompress(body);

            // 反序列化，protostuff or kryo
            String coderName = SerializationTypeEnum.getName(coderType);
            Serializer serializer =
                    ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(coderName);
            if (messageType == RpcConstants.REQUEST_TYPE) {  // 区分请求报文和响应报文
                rpcMessage.setData(serializer.deserialize(body, RpcRequest.class));
            } else {
                rpcMessage.setData(serializer.deserialize(body, RpcResponse.class));
            }
        }
        return rpcMessage;
    }

    // 检查版本号
    private void checkVersion(ByteBuf in) {
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            log.error("version error");
            throw new IllegalArgumentException("version error : " + version);
        }
    }

    // 检查魔数
    private void checkMagicCode(ByteBuf in) {
        byte[] bytes = new byte[RpcConstants.MAGIC_CODE.length];
        in.readBytes(bytes);
        for (int i = 0; i < bytes.length; i++)
            if (bytes[i] != RpcConstants.MAGIC_CODE[i]) {
                log.error("magic code error");
                throw new IllegalArgumentException("magic code error : " + Arrays.toString(bytes));
            }
    }
}
