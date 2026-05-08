package run.soeasy.framework.codec.binary;

import java.io.IOException;
import java.io.InputStream;

import lombok.NonNull;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.io.BinaryTransferrer;

/**
 * 链式二进制编码器接口，继承自{@link BinaryEncoder}和{@link ChainFromBinaryEncoder}，
 * 实现二进制数据到二进制数据的链式编码，通过组合两个二进制编码器完成多步编码转换，
 * 适用于需要对二进制数据进行多次编码处理的场景。
 * 
 * <p>该接口的核心是将两个二进制编码器串联使用，先通过第一个编码器处理原始二进制数据，
 * 再将处理结果传递给第二个编码器进行二次处理，形成完整的链式编码流程。
 * 
 * @see BinaryEncoder
 * @see ChainFromBinaryEncoder
 */
public interface ChainBinaryEncoder extends BinaryEncoder, ChainFromBinaryEncoder<byte[], byte[]> {

    /**
     * 获取第一个二进制编码器（用于初步编码）
     * 
     * @return 第一个二进制编码器实例
     */
    @Override
    BinaryEncoder getFromEncoder();

    /**
     * 获取第二个二进制编码器（用于二次编码）
     * 
     * @return 第二个二进制编码器实例
     */
    @Override
    BinaryEncoder getToEncoder();

    /**
     * 对二进制数组进行链式编码
     * 
     * @param source 待编码的二进制数组
     * @return 经过两次编码后的二进制数组
     * @throws CodecException 编码过程发生错误时抛出
     */
    @Override
    default byte[] encode(byte[] source) throws CodecException {
        return ChainFromBinaryEncoder.super.encode(source);
    }

    /**
     * 对输入流数据进行链式编码
     * 
     * @param source 待编码的输入流
     * @param bufferSize 读取缓冲区大小
     * @return 经过两次编码后的二进制数组
     * @throws IOException 输入流读取失败时抛出
     * @throws CodecException 编码过程发生错误时抛出
     */
    @Override
    default byte[] encode(@NonNull InputStream source, int bufferSize) throws IOException, CodecException {
        return ChainFromBinaryEncoder.super.encode(source, bufferSize);
    }

    /**
     * 获取链式编码的二进制传输器
     * 
     * <p>将两个编码器的传输器串联，形成完整的链式传输流程
     * 
     * @return 组合后的二进制传输器
     */
    @Override
    default BinaryTransferrer getEncodeTransferrer() {
        return getFromEncoder().getEncodeTransferrer().to(getToEncoder().getEncodeTransferrer());
    }
}