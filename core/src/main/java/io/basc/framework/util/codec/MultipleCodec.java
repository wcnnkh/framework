package io.basc.framework.util.codec;

public interface MultipleCodec<T> extends Codec<T, T>, MultipleEncoder<T>, MultipleDecoder<T> {

	@Override
	default MultipleCodec<T> multiple(int count) {
		return new NestedMultipleCodec<>(this, count);
	}
}
