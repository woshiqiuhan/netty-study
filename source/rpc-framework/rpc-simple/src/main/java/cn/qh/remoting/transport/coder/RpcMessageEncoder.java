package cn.qh.remoting.transport.coder;


import cn.qh.compress.Compress;
import cn.qh.enums.CompressTypeEnum;
import cn.qh.enums.SerializationTypeEnum;
import cn.qh.extension.ExtensionLoader;
import cn.qh.remoting.constants.RpcConstants;
import cn.qh.remoting.dto.RpcMessage;
import cn.qh.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;


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
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 */

@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    // 原子自增 id
    private final static AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf byteBuf) throws Exception {
        try {
            byteBuf.writeBytes(RpcConstants.MAGIC_CODE);
            byteBuf.writeByte(RpcConstants.VERSION);
            byteBuf.writerIndex(byteBuf.writerIndex() + Integer.BYTES);
            byteBuf.writeByte(rpcMessage.getMessageType());
            byteBuf.writeByte(rpcMessage.getCoderType());
            byteBuf.writeByte(rpcMessage.getCompressType());
            byteBuf.writeInt(ATOMIC_INTEGER.incrementAndGet());

            byte[] body = null;
            // 存储报文总长度
            int fullLength = RpcConstants.HEAD_LENGTH;
            // 如果消息类型非 心跳检测类型
            if (rpcMessage.getMessageType() != RpcConstants.HEART_REQUEST
                    && rpcMessage.getMessageType() != RpcConstants.HEART_RESPONSE) {

                // 对数据进行序列化
                String coderName = SerializationTypeEnum.getName(rpcMessage.getCoderType());
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(coderName);

                body = serializer.serialize(rpcMessage.getData());

                // 对序列化后数据进行压缩
                String compressName = CompressTypeEnum.getName(rpcMessage.getCompressType());
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);

                body = compress.compress(body);

                // 加上数据长度
                fullLength += body.length;
            }

            if (body != null) {
                byteBuf.writeBytes(body);
            }

            // 找到写入长度到位置
            int i = byteBuf.writerIndex();
            byteBuf.writerIndex(i - fullLength + RpcConstants.MAGIC_CODE.length + RpcConstants.VERSION_LENGTH);
            byteBuf.writeInt(fullLength);
            // 复位
            byteBuf.writerIndex(i);
        } catch (Exception e) {
            log.error("encode request failed");
        }
    }
}

