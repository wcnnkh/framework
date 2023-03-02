package io.basc.framework.codec;

public interface Codec<D, E> extends Encoder<D, E>, Decoder<E, D> {

	default <F> Codec<F, E> from(Codec<F, D> codec) {
		return new NestedCodec<>(codec, this);
	}

	default <T> Codec<D, T> to(Codec<E, T> codec) {
		return new NestedCodec<>(this, codec);
	}

	default Codec<E, D> reversal() {
		return new ReversalCodec<E, D>(this);
	}

	public static <R> Codec<R, R> identity() {
		return new IdentityCodec<>();
	}
}
