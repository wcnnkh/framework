package io.basc.framework.codec;

/**
 * 多次编码器
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public interface MultipleEncoder<E> extends Encoder<E, E> {

	default int getCount() {
		return 1;
	}

	@Override
	default E encode(E source) throws EncodeException {
		return encode(source, getCount());
	}

	/**
	 * 进行指定数据的编码
	 * 
	 * @param source
	 * @param count  次数
	 * @return
	 * @throws EncodeException
	 */
	E encode(E source, int count) throws EncodeException;

	/**
	 * 多次操作
	 * 
	 * @param count
	 * @return
	 */
	default MultipleEncoder<E> multiple(int count) {
		return new NestedMultipleEncoder<>(this, count);
	}
}
