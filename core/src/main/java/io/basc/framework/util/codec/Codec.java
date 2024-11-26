package io.basc.framework.util.codec;

public interface Codec<D, E> extends Encoder<D, E>, Decoder<E, D> {

	@SuppressWarnings("unchecked")
	public static <R> Codec<R, R> identity() {
		return (Codec<R, R>) IdentityCodec.INSTANCE;
	}

	default <F> Codec<F, E> from(Codec<F, D> codec) {
		return new NestedCodec<>(codec, this);
	}

	default Codec<E, D> reverse() {
		return new ReverseCodec<E, D>(this);
	}

	default <T> Codec<D, T> to(Codec<E, T> codec) {
		return new NestedCodec<>(this, codec);
	}
}
