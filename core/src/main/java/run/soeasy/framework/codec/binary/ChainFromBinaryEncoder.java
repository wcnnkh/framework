package run.soeasy.framework.codec.binary;

import java.io.IOException;
import java.io.InputStream;

import lombok.NonNull;
import run.soeasy.framework.codec.ChainEncoder;
import run.soeasy.framework.codec.CodecException;

/**
 * 链式二进制到目标类型编码器接口，继承自{@link FromBinaryEncoder}和{@link ChainEncoder}，
 * 实现从二进制数据（byte[]或InputStream）到中间类型T再到目标类型E的链式编码，
 * 适用于需要多步转换的二进制数据编码场景。
 * 
 * <p>该接口的核心逻辑是将编码过程分为两个阶段：
 * 1. 从二进制数据（byte[]或InputStream）编码为中间类型T（通过{@link #getFromEncoder()}）
 * 2. 从中间类型T编码为目标类型E（通过{@link ChainEncoder#getToEncoder()}）
 * 两阶段衔接形成完整的二进制到目标类型的链式编码流程。
 * 
 * @param <T> 中间数据类型（第一阶段编码结果，第二阶段编码输入）
 * @param <E> 目标数据类型（最终编码结果类型）
 * @author soeasy.run
 * @see FromBinaryEncoder
 * @see ChainEncoder
 */
public interface ChainFromBinaryEncoder<T, E> extends FromBinaryEncoder<E>, ChainEncoder<byte[], T, E> {

    /**
     * 获取从二进制数据到中间类型T的编码器
     * 
     * @return 二进制到中间类型的编码器
     */
    @Override
    FromBinaryEncoder<T> getFromEncoder();

    /**
     * 将二进制数组编码为目标类型E（链式实现）
     * 
     * <p>默认调用{@link ChainEncoder#super.encode(byte[])}，实现"byte[]→T→E"的链式编码
     * 
     * @param source 待编码的二进制数组
     * @return 编码后的目标类型E实例
     * @throws CodecException 当编码过程发生错误时抛出
     */
    @Override
    default E encode(byte[] source) throws CodecException {
        return ChainEncoder.super.encode(source);
    }

    /**
     * 将输入流数据编码为目标类型E（链式实现）
     * 
     * <p>编码流程：
     * 1. 通过{@link #getFromEncoder()}将输入流编码为中间类型T
     * 2. 通过{@link ChainEncoder#getToEncoder()}将T编码为目标类型E
     * 
     * @param source 待编码的输入流（非空）
     * @param bufferSize 读取缓冲区大小
     * @return 编码后的目标类型E实例
     * @throws IOException 当输入流读取发生I/O错误时抛出
     * @throws CodecException 当编码过程发生错误时抛出
     */
    @Override
    default E encode(@NonNull InputStream source, int bufferSize) throws IOException, CodecException {
        T encode = getFromEncoder().encode(source, bufferSize);
        return getToEncoder().encode(encode);
    }
}