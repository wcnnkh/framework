package run.soeasy.framework.codec.binary;

import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.MultipleCodecWrapper;

/**
 * 二进制编解码器包装器接口，继承自{@link BinaryCodec}、{@link BinaryDecoderWrapper}、
 * {@link BinaryEncoderWrapper}和{@link MultipleCodecWrapper}，提供对二进制编解码器的包装能力，
 * 支持编解码操作的代理和多重复用，是框架中处理二进制数据编解码包装的核心接口。
 * 
 * <p>该接口整合了二进制编解码和包装器的功能，通过代理底层二进制编解码器实现编解码操作，
 * 同时支持设置多重复用次数，适用于需要对二进制编解码器进行增强或扩展的场景。
 * 
 * @param <W> 被包装的二进制编解码器类型
 * @author soeasy.run
 * @see BinaryCodec
 * @see BinaryDecoderWrapper
 * @see BinaryEncoderWrapper
 * @see MultipleCodecWrapper
 */
public interface BinaryCodecWrapper<W extends BinaryCodec>
		extends BinaryCodec, BinaryDecoderWrapper<W>, BinaryEncoderWrapper<W>, MultipleCodecWrapper<byte[], W> {

	/**
	 * 创建多重复用的二进制编解码器
	 * 
	 * @param count 复用次数乘数
	 * @return 多重复用的二进制编解码器
	 */
	@Override
	default BinaryCodec multiple(int count) {
		return getSource().multiple(count * getMultiple());
	}

	/**
	 * 对二进制数据进行编码（代理实现）
	 * 
	 * @param source 待编码的二进制数据
	 * @return 编码后的二进制数据
	 * @throws CodecException 当编码过程中发生错误时抛出
	 */
	@Override
	default byte[] encode(byte[] source) throws CodecException {
		return BinaryEncoderWrapper.super.encode(source);
	}

	/**
	 * 对二进制数据进行解码（代理实现）
	 * 
	 * @param source 待解码的二进制数据
	 * @return 解码后的二进制数据
	 * @throws CodecException 当解码过程中发生错误时抛出
	 */
	@Override
	default byte[] decode(byte[] source) throws CodecException {
		return BinaryDecoderWrapper.super.decode(source);
	}

}