package io.basc.framework.util.codec;

public interface CodecWrapper<D, E, W extends Codec<D, E>>
		extends Codec<D, E>, EncoderWrapper<D, E, W>, DecoderWrapper<E, D, W> {
	@Override
	default <T> Codec<D, T> to(Codec<E, T> codec) {
		return getSource().to(codec);
	}

	@Override
	default <F> Codec<F, E> from(Codec<F, D> codec) {
		return getSource().from(codec);
	}
}
