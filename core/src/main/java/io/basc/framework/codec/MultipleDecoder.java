package io.basc.framework.codec;

/**
 * 多次解码器
 * 
 * @author shuchaowen
 *
 * @param <D>
 */
public interface MultipleDecoder<D> extends Decoder<D, D> {
	/**
	 * 进行多次解码
	 * 
	 * @param source
	 * @param count
	 * @return
	 * @throws DecodeException
	 */
	default D decode(D source, int count) throws DecodeException {
		D v = source;
		for (int i = 0; i < count; i++) {
			v = decode(v);
		}
		return v;
	}

	/**
	 * 多次操作
	 * 
	 * @param count
	 * @return
	 */
	default MultipleDecoder<D> multiple(int count) {
		return new NestedMultipleDecoder<>(this, count);
	}
}
