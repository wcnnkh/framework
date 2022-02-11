package io.basc.framework.codec;

/**
 * 多次编解码
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface MultipleCodec<T> extends Codec<T, T>, MultipleEncoder<T>, MultipleDecoder<T> {

	@Override
	default int getCount() {
		return 1;
	}

	/**
	 * 多次操作
	 */
	@Override
	default MultipleCodec<T> multiple(int count) {
		return new NestedMultipleCodec<>(this, count);
	}

	@Override
	default T encode(T source) throws EncodeException {
		return MultipleEncoder.super.encode(source);
	}

	@Override
	default T decode(T source) throws DecodeException {
		return MultipleDecoder.super.decode(source);
	}
}
