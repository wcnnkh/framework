package run.soeasy.framework.codec.binary;

import lombok.NonNull;
import run.soeasy.framework.codec.ChainCodec;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.io.BinaryTransferrer;

/**
 * 链式二进制编解码器接口，继承自{@link BinaryCodec}、{@link ChainBinaryEncoder}、
 * {@link ChainBinaryDecoder}和{@link ChainCodec}，整合了二进制数据的链式编码与解码能力，
 * 实现字节数组（byte[]）的双向多步转换，适用于需要多阶段处理的二进制数据编解码场景。
 * 
 * <p>该接口通过组合链式编码器和解码器的功能，提供统一的二进制数据双向处理能力，
 * 编码流程为"原始byte[]→中间byte[]→目标byte[]"，解码流程为"目标byte[]→中间byte[]→原始byte[]"，
 * 支持复杂二进制数据的分步转换处理。
 * 
 * @see BinaryCodec
 * @see ChainBinaryEncoder
 * @see ChainBinaryDecoder
 * @see ChainCodec
 */
public interface ChainBinaryCodec
		extends BinaryCodec, ChainBinaryEncoder, ChainBinaryDecoder, ChainCodec<byte[], byte[], byte[]> {

	/**
	 * 对二进制数组进行链式编码（委托实现）
	 * 
	 * @param source 待编码的二进制数组
	 * @return 链式编码后的二进制数组
	 * @throws CodecException 编码过程发生错误时抛出
	 */
	@Override
	default byte[] encode(byte[] source) throws CodecException {
		return ChainBinaryEncoder.super.encode(source);
	}

	/**
	 * 对二进制数组进行链式解码（委托实现）
	 * 
	 * @param source 待解码的二进制数组
	 * @return 链式解码后的二进制数组
	 * @throws CodecException 解码过程发生错误时抛出
	 */
	@Override
	default byte[] decode(@NonNull byte[] source) throws CodecException {
		return ChainBinaryDecoder.super.decode(source);
	}

	/**
	 * 获取链式解码的二进制传输器
	 * 
	 * <p>组合第一个解码器和第二个解码器的传输器，形成完整的链式解码传输流程
	 * 
	 * @return 链式解码传输器
	 */
	@Override
	default BinaryTransferrer getDecodeTransferrer() {
		return getFromDecoder().getDecodeTransferrer().to(getToDecoder().getDecodeTransferrer());
	}

	/**
	 * 获取链式编码的二进制传输器
	 * 
	 * <p>组合第一个编码器和第二个编码器的传输器，形成完整的链式编码传输流程
	 * 
	 * @return 链式编码传输器
	 */
	@Override
	default BinaryTransferrer getEncodeTransferrer() {
		return getFromEncoder().getEncodeTransferrer().to(getToEncoder().getEncodeTransferrer());
	}

}