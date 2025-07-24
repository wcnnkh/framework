package run.soeasy.framework.codec.binary;

import java.io.IOException;
import java.io.InputStream;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.io.BinaryTransferrer;

/**
 * 链式二进制解码器接口，继承自{@link BinaryDecoder}和{@link ChainFromBinaryDecoder}，
 * 实现二进制数据到二进制数据的链式解码，通过组合两个二进制解码器完成多步解码转换，
 * 适用于需要对二进制数据进行多次解码处理的场景。
 * 
 * <p>该接口的核心是将两个二进制解码器串联使用，先通过第一个解码器处理原始二进制数据，
 * 再将处理结果传递给第二个解码器进行二次处理，形成完整的链式解码流程。
 * 
 * @see BinaryDecoder
 * @see ChainFromBinaryDecoder
 */
public interface ChainBinaryDecoder extends BinaryDecoder, ChainFromBinaryDecoder<byte[], byte[]> {

    /**
     * 获取第一个二进制解码器（用于初步解码）
     * 
     * @return 第一个二进制解码器实例
     */
    @Override
    BinaryDecoder getFromDecoder();

    /**
     * 获取第二个二进制解码器（用于二次解码）
     * 
     * @return 第二个二进制解码器实例
     */
    @Override
    BinaryDecoder getToDecoder();

    /**
     * 对二进制数组进行链式解码
     * 
     * @param source 待解码的二进制数组
     * @return 经过两次解码后的二进制数组
     * @throws CodecException 解码过程发生错误时抛出
     */
    @Override
    default byte[] decode(@NonNull byte[] source) throws CodecException {
        return ChainFromBinaryDecoder.super.decode(source);
    }

    /**
     * 对输入流数据进行链式解码
     * 
     * @param source 待解码的输入流
     * @param bufferSize 读取缓冲区大小
     * @return 经过两次解码后的二进制数组
     * @throws IOException 输入流读取失败时抛出
     * @throws CodecException 解码过程发生错误时抛出
     */
    @Override
    default byte[] decode(@NonNull InputStream source, int bufferSize) throws IOException, CodecException {
        return ChainFromBinaryDecoder.super.decode(source, bufferSize);
    }

    /**
     * 获取链式解码的二进制传输器
     * 
     * <p>将两个解码器的传输器串联，形成完整的链式传输流程
     * 
     * @return 组合后的二进制传输器
     */
    @Override
    default BinaryTransferrer getDecodeTransferrer() {
        return getFromDecoder().getDecodeTransferrer().to(getToDecoder().getDecodeTransferrer());
    }
}