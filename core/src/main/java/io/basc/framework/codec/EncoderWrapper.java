package io.basc.framework.codec;

import io.basc.framework.util.Wrapper;

public interface EncoderWrapper<D, E, W extends Encoder<D, E>> extends Encoder<D, E>, Wrapper<W> {
	@Override
	default E encode(D source) throws EncodeException {
		return getSource().encode(source);
	}

	@Override
	default boolean verify(D source, E encode) throws EncodeException {
		return verify(source, encode);
	}

	@Override
	default <F> Encoder<F, E> fromEncoder(Encoder<F, D> encoder) {
		return getSource().fromEncoder(encoder);
	}

	@Override
	default <T> Encoder<D, T> toEncoder(Encoder<E, T> encoder) {
		return getSource().toEncoder(encoder);
	}
}
