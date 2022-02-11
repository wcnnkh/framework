package io.basc.framework.codec;

/**
 * 多次解码器
 * 
 * @author shuchaowen
 *
 * @param <D>
 */
public interface MultipleDecoder<D> extends Decoder<D, D> {
	default int getCount() {
		return 1;
	}

	@Override
	default D decode(D source) throws DecodeException {
		return decode(source, getCount());
	}

	/**
	 * 进行多次解码
	 * 
	 * @param source
	 * @param count
	 * @return
	 * @throws DecodeException
	 */
	D decode(D source, int count) throws DecodeException;

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
