package io.basc.framework.codec;

/**
 * 多次编码器
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public interface MultipleEncoder<E> extends Encoder<E, E> {

	/**
	 * 进行指定数据的多次编码
	 * 
	 * @param source
	 * @param count  次数
	 * @return
	 * @throws EncodeException
	 */
	default E encode(E source, int count) throws EncodeException {
		E e = source;
		for (int i = 0; i < count; i++) {
			e = encode(e);
		}
		return e;
	}

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
