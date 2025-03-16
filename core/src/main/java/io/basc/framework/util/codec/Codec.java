package io.basc.framework.util.codec;

public interface Codec<D, E> extends Encoder<D, E>, Decoder<E, D> {

	public static interface CodecWrapper<D, E, W extends Codec<D, E>>
			extends Codec<D, E>, EncoderWrapper<D, E, W>, DecoderWrapper<E, D, W> {
		@Override
		default <T> Codec<D, T> to(Codec<E, T> codec) {
			return getSource().to(codec);
		}

		@Override
		default <F> Codec<F, E> from(Codec<F, D> codec) {
			return getSource().from(codec);
		}

		@Override
		default Codec<E, D> reverse() {
			return getSource().reverse();
		}
	}

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
