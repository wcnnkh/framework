package io.basc.framework.util.codec;

public interface MultipleCodec<T> extends Codec<T, T>, MultipleEncoder<T>, MultipleDecoder<T> {
	public static interface MultipleCodecWrapper<T, W extends MultipleCodec<T>> extends MultipleCodec<T>,
			CodecWrapper<T, T, W>, MultipleEncoderWrapper<T, W>, MultipleDecoderWrapper<T, W> {

		@Override
		default MultipleCodec<T> multiple(int count) {
			return getSource().multiple(count);
		}

	}

	@Override
	default MultipleCodec<T> multiple(int count) {
		return new NestedMultipleCodec<>(this, count);
	}
}
