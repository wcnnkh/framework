package run.soeasy.framework.codec.binary;

import java.io.IOException;
import java.io.InputStream;

import lombok.NonNull;
import run.soeasy.framework.codec.ChainDecoder;
import run.soeasy.framework.codec.CodecException;

/**
 * 链式二进制到目标类型解码器接口，继承自{@link FromBinaryDecoder}和{@link ChainDecoder}，
 * 提供从二进制数据到目标类型的两步阶段解码能力，支持 * 适用于阶段分别处理二进制到中间类型、中间类型到目标类型的转换。
 * 
 * @param <T> 中间数据类型（第一阶段解码结果）
 * @param <E> 目标数据类型（最终解码结果）
 * @see FromBinaryDecoder
 * @see ChainDecoder
 */
public interface ChainFromBinaryDecoder<T, E> extends FromBinaryDecoder<E>, ChainDecoder<byte[], T, E> {

    /**
     * 获取二进制到中间类型的解码器
     * 
     * @return 二进制到中间类型的解码器实例
     */
    @Override
    FromBinaryDecoder<T> getFromDecoder();

    /**
     * 对二进制数组进行链式解码
     * 
     * @param source 待解码的二进制数组
     * @return 解码后的目标类型对象
     * @throws CodecException 解码过程发生错误时抛出
     */
    @Override
    default E decode(@NonNull byte[] source) throws CodecException {
        return ChainDecoder.super.decode(source);
    }

    /**
     * 对输入流数据进行链式解码
     * 
     * @param source 待解码的输入流
     * @param bufferSize 读取缓冲区大小
     * @return 解码后的目标类型对象
     * @throws IOException 输入流读取失败时抛出
     * @throws CodecException 解码过程发生错误时抛出
     */
    @Override
    default E decode(@NonNull InputStream source, int bufferSize) throws IOException, CodecException {
        T decode = getFromDecoder().decode(source, bufferSize);
        return getToDecoder().decode(decode);
    }

}