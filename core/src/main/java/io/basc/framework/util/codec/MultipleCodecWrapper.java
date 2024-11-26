package io.basc.framework.util.codec;

public interface MultipleCodecWrapper<T, W extends MultipleCodec<T>>
		extends MultipleCodec<T>, CodecWrapper<T, T, W>, MultipleEncoderWrapper<T, W>, MultipleDecoderWrapper<T, W> {

	@Override
	default MultipleCodec<T> multiple(int count) {
		return getSource().multiple(count);
	}

}
