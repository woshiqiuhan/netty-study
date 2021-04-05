package cn.qh.compress;

import cn.qh.extension.SPI;

// 压缩报文、解压报文
@SPI
public interface Compress {
    // 压缩
    byte[] compress(byte[] bytes);

    // 解压
    byte[] decompress(byte[] bytes);
}
