package run.soeasy.framework.codec.binary;

import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.Encoder;
import run.soeasy.framework.codec.MultipleEncoderWrapper;
import run.soeasy.framework.io.BinaryTransferrer;

/**
 * 二进制编码器包装器接口，继承自{@link BinaryEncoder}和{@link MultipleEncoderWrapper}，
 * 提供对二进制编码器的包装能力，支持编码操作的代理、多重复用及与其他编码器的组合，
 * 是框架中处理二进制数据编码包装的核心接口。
 * 
 * <p>该接口通过代理底层二进制编码器实现编码功能，并扩展了多重复用、编码验证及编码器转换能力，
 * 适用于需要对二进制编码过程进行增强或适配的场景。
 * 
 * @param <W> 被包装的二进制编码器类型
 * @author soeasy.run
 * @see BinaryEncoder
 * @see MultipleEncoderWrapper
 * @see BinaryTransferrer
 */
public interface BinaryEncoderWrapper<W extends BinaryEncoder> extends BinaryEncoder, MultipleEncoderWrapper<byte[], W> {

    /**
     * 对二进制数据进行编码（代理实现）
     * 
     * <p>默认通过{@link MultipleEncoderWrapper#encode(Object)}代理到底层编码器
     * 
     * @param source 待编码的二进制数据
     * @return 编码后的二进制数据
     * @throws CodecException 当编码过程中发生错误时抛出
     */
    @Override
    default byte[] encode(byte[] source) throws CodecException {
        return MultipleEncoderWrapper.super.encode(source);
    }

    /**
     * 验证编码结果是否正确
     * 
     * @param source 原始数据
     * @param encode 编码后的数据
     * @return 验证通过返回true，否则返回false
     * @throws CodecException 当验证过程中发生错误时抛出
     */
    @Override
    default boolean test(byte[] source, byte[] encode) throws CodecException {
        return BinaryEncoder.super.test(source, encode);
    }

    /**
     * 将当前二进制编码器转换为目标类型编码器
     * 
     * @param <T> 目标数据类型
     * @param encoder 字节数组到目标类型的编码器
     * @return 组合后的二进制到目标类型编码器
     */
    @Override
    default <T> FromBinaryEncoder<T> toEncoder(Encoder<byte[], T> encoder) {
        return BinaryEncoder.super.toEncoder(encoder);
    }

    /**
     * 创建指定重复次数的二进制编码器
     * 
     * @param count 重复次数乘数
     * @return 多重复用的二进制编码器
     */
    @Override
    default BinaryEncoder multiple(int count) {
        return getSource().multiple(count * getEncodeMultiple());
    }

    /**
     * 获取编码用的二进制传输器（带重复次数）
     * 
     * <p>基于底层编码器的传输器并应用当前的编码重复次数
     * 
     * @return 配置后的二进制传输器
     */
    @Override
    default BinaryTransferrer getEncodeTransferrer() {
        return getSource().getEncodeTransferrer().repeat(getEncodeMultiple());
    }
}