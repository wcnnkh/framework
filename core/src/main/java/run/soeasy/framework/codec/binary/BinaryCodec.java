package run.soeasy.framework.codec.binary;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.codec.MultipleCodec;

/**
 * 字节数组编解码器接口，整合{@link MultipleCodec}、{@link BinaryEncoder}、{@link BinaryDecoder}、
 * {@link ToBinaryCodec}和{@link FromBinaryCodec}功能，提供字节数组的双向编解码能力，
 * 支持多次编解码操作和编解码器组合，适用于复杂的二进制数据处理场景。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>全功能整合：同时具备编码、解码、多次编解码和格式转换能力</li>
 * <li>双向转换：支持字节数组与自身的双向编解码操作</li>
 * <li>多次操作：通过{@link #multiple(int)}支持指定次数的连续编解码</li>
 * <li>链式组合：通过{@link #to(BinaryCodec)}/{@link #from(BinaryCodec)}组合编解码器</li>
 * <li>格式扩展：支持转换为Base64等常见格式（如{@link #toBase64()}）</li>
 * </ul>
 * 
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>多层编解码流程：如"压缩→加密→Base64编码"的多级处理</li>
 * <li>二进制数据处理：图片/视频的编解码、文件加解密</li>
 * <li>网络传输优化：组合编解码器实现数据压缩与安全传输</li>
 * <li>复杂格式转换：结合多种编码格式实现数据格式适配</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see MultipleCodec
 * @see BinaryEncoder
 * @see BinaryDecoder
 * @see ToBinaryCodec
 * @see FromBinaryCodec
 */
public interface BinaryCodec
		extends MultipleCodec<byte[]>, BinaryEncoder, BinaryDecoder, ToBinaryCodec<byte[]>, FromBinaryCodec<byte[]> {

	@Override
	default byte[] encode(byte[] source) throws CodecException {
		return BinaryEncoder.super.encode(source);
	}

	@Override
	default byte[] decode(byte[] source) throws CodecException {
		return BinaryDecoder.super.decode(source);
	}

	/**
	 * 组合后置编解码器形成新的字节编解码器（装饰器模式）。
	 * <p>
	 * 新编解码器的编解码流程为：
	 * 
	 * <pre>
	 * 编码：byte[] → [当前编解码器] → byte[] → [后置编解码器] → byte[]
	 * 解码：byte[] → [后置编解码器] → byte[] → [当前编解码器] → byte[]
	 * </pre>
	 * 
	 * @param codec 后置编解码器，不可为null
	 * @return 组合后的新编解码器
	 */
	default BinaryCodec to(BinaryCodec codec) {
		return new ChainBinaryCodec() {

			@Override
			public BinaryDecoder getToDecoder() {
				return BinaryCodec.this;
			}

			@Override
			public BinaryDecoder getFromDecoder() {
				return codec;
			}

			@Override
			public BinaryEncoder getToEncoder() {
				return codec;
			}

			@Override
			public BinaryEncoder getFromEncoder() {
				return BinaryCodec.this;
			}
		};
	}

	/**
	 * 组合前置编解码器形成新的字节编解码器（装饰器模式）。
	 * <p>
	 * 新编解码器的编解码流程为：
	 * 
	 * <pre>
	 * 编码：byte[] → [前置编解码器] → byte[] → [当前编解码器] → byte[]
	 * 解码：byte[] → [当前编解码器] → byte[] → [前置编解码器] → byte[]
	 * </pre>
	 * 
	 * @param codec 前置编解码器，不可为null
	 * @return 组合后的新编解码器
	 */
	default BinaryCodec from(BinaryCodec codec) {
		return new ChainBinaryCodec() {

			@Override
			public BinaryDecoder getToDecoder() {
				return codec;
			}

			@Override
			public BinaryDecoder getFromDecoder() {
				return BinaryCodec.this;
			}

			@Override
			public BinaryEncoder getToEncoder() {
				return BinaryCodec.this;
			}

			@Override
			public BinaryEncoder getFromEncoder() {
				return codec;
			}
		};
	}

	/**
	 * 创建固定执行次数的多重编解码器（装饰器模式）。
	 * <p>
	 * 返回的新编解码器在调用{@link #encode(Object)}或{@link #decode(Object)}时，
	 * 会自动执行当前编解码器指定次数的编解码操作，等价于：
	 * 
	 * <pre>
	 * for (int i = 0; i &lt; count; i++) {
	 * 	data = encode(data); // 或 decode(data)
	 * }
	 * </pre>
	 * 
	 * @param count 固定编解码次数（≥0）
	 * @return 新的多重编解码器实例
	 * @throws IllegalArgumentException 当count&lt;0时抛出
	 */
	@Override
	default BinaryCodec multiple(int count) {
		return new BinaryCodecWrapper<BinaryCodec>() {

			@Override
			public BinaryCodec getSource() {
				return BinaryCodec.this;
			}

			@Override
			public int getMultiple() {
				return count;
			}
		};
	}

	/**
	 * 组合Base64编解码器形成新的编解码器（继承自{@link ToBinaryCodec}）。
	 * <p>
	 * 新编解码器的编解码流程为：
	 * 
	 * <pre>
	 * 编码：byte[] → [当前编解码器] → byte[] → [Base64编解码器] → String
	 * 解码：String → [Base64编解码器] → byte[] → [当前编解码器] → byte[]
	 * </pre>
	 * 
	 * @return 组合后的Base64编解码器，不可为null
	 */
	@Override
	default Codec<byte[], String> toBase64() {
		return ToBinaryCodec.super.toBase64();
	}
}