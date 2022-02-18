package io.basc.framework.codec;

/**
 * 多次编解码
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface MultipleCodec<T> extends Codec<T, T>, MultipleEncoder<T>, MultipleDecoder<T> {

	/**
	 * 多次操作
	 */
	@Override
	default MultipleCodec<T> multiple(int count) {
		return new NestedMultipleCodec<>(this, count);
	}
}
