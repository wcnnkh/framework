package io.basc.framework.util.codec;

import io.basc.framework.util.function.Wrapper;

public interface DecoderWrapper<E, D, W extends Decoder<E, D>> extends Decoder<E, D>, Wrapper<W> {
	@Override
	default D decode(E source) throws DecodeException {
		return getSource().decode(source);
	}

	@Override
	default <F> Decoder<F, D> fromDecoder(Decoder<F, E> decoder) {
		return getSource().fromDecoder(decoder);
	}

	@Override
	default <T> Decoder<E, T> toDecoder(Decoder<D, T> decoder) {
		return getSource().toDecoder(decoder);
	}
}
