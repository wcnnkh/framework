package run.soeasy.framework.codec.io;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.format.Base64;
import run.soeasy.framework.codec.format.HexCodec;

/**
 * 序列化器接口，定义了将对象与字节数组互转的规范。
 * <p>
 * 该接口继承自 {@code Codec<T, byte[]>}，并提供了 Base64 编码、 Hex
 * 编码等链式处理能力，同时支持通过序列化机制实现对象的深拷贝。
 * </p>
 *
 * @param <T> 序列化/反序列化的目标对象类型
 */
public interface Serializer<T> extends Codec<T, byte[]> {
	/**
	 * 获取字节缓冲区编码器。
	 *
	 * @return 编码器实例
	 */
	@Override
	ByteBufferEncoder<T> encoder();

	/**
	 * 获取字节缓冲区解码器。
	 *
	 * @return 解码器实例
	 */
	@Override
	ByteBufferDecoder<T> decoder();

	/**
	 * 返回一个将序列化结果转换为 Base64 字符串的编解码器。
	 *
	 * @return Base64 编码的 Codec
	 */
	default Codec<T, String> toBase64() {
		return to(Base64.DEFAULT);
	}

	/**
	 * 返回一个将序列化结果转换为十六进制字符串的编解码器。
	 *
	 * @return Hex 编码的 Codec
	 */
	default Codec<T, String> toHex() {
		return to(HexCodec.DEFAULT);
	}

	/**
	 * 通过序列化和反序列化实现对象的深拷贝。
	 * <p>
	 * 注意：此方法要求序列化实现能正确处理对象图，且性能不如专用拷贝工具。 如果源对象为 {@code null}，则返回 {@code null}。
	 * </p>
	 *
	 * @param source 源对象
	 * @return 拷贝后的新对象，或 {@code null}
	 */
	default T clone(T source) {
		if (source == null) {
			return null;
		}
		byte[] data = encode(source);
		return decode(data);
	}
}
