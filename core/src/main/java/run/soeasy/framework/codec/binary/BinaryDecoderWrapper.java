package run.soeasy.framework.codec.binary;

import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.Decoder;
import run.soeasy.framework.codec.MultipleDecoderWrapper;
import run.soeasy.framework.io.BinaryTransferrer;

/**
 * 二进制解码器包装器接口，继承自{@link BinaryDecoder}和{@link MultipleDecoderWrapper}，
 * 提供对二进制解码器的包装能力，支持解码操作的代理、多重复用及与其他解码器的组合，
 * 是框架中处理二进制数据解码包装的核心接口。
 * 
 * <p>该接口通过代理底层二进制解码器实现解码功能，并扩展了多重复用和解码器转换能力，
 * 适用于需要对二进制解码过程进行增强或适配的场景。
 * 
 * @param <W> 被包装的二进制解码器类型
 * @author soeasy.run
 * @see BinaryDecoder
 * @see MultipleDecoderWrapper
 * @see BinaryTransferrer
 */
public interface BinaryDecoderWrapper<W extends BinaryDecoder> extends BinaryDecoder, MultipleDecoderWrapper<byte[], W> {

    /**
     * 对二进制数据进行解码（代理实现）
     * 
     * <p>默认通过{@link MultipleDecoderWrapper#decode(Object)}代理到底层解码器
     * 
     * @param source 待解码的二进制数据
     * @return 解码后的二进制数据
     * @throws CodecException 当解码过程中发生错误时抛出
     */
    @Override
    default byte[] decode(byte[] source) throws CodecException {
        return MultipleDecoderWrapper.super.decode(source);
    }

    /**
     * 将当前二进制解码器转换为目标类型解码器
     * 
     * @param <T> 目标数据类型
     * @param decoder 字节数组到目标类型的解码器
     * @return 组合后的二进制到目标类型解码器
     */
    @Override
    default <T> FromBinaryDecoder<T> toDecoder(Decoder<byte[], T> decoder) {
        return BinaryDecoder.super.toDecoder(decoder);
    }

    /**
     * 获取解码用的二进制传输器（带重复次数）
     * 
     * <p>基于底层解码器的传输器并应用当前的解码重复次数
     * 
     * @return 配置后的二进制传输器
     */
    @Override
    default BinaryTransferrer getDecodeTransferrer() {
        return getSource().getDecodeTransferrer().repeat(getDecodeMultiple());
    }

    /**
     * 创建指定重复次数的二进制解码器
     * 
     * @param count 重复次数乘数
     * @return 多重复用的二进制解码器
     */
    @Override
    default BinaryDecoder multiple(int count) {
        return getSource().multiple(count * getDecodeMultiple());
    }
}