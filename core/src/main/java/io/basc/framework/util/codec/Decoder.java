package io.basc.framework.util.codec;

import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.function.Wrapper;
import lombok.NonNull;

@FunctionalInterface
public interface Decoder<E, D> {

	public static interface DecoderWrapper<E, D, W extends Decoder<E, D>> extends Decoder<E, D>, Wrapper<W> {
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

		@Override
		default Elements<D> decodeAll(@NonNull Elements<? extends E> sources) throws DecodeException {
			return getSource().decodeAll(sources);
		}
	}

	D decode(E source) throws DecodeException;

	default Elements<D> decodeAll(@NonNull Elements<? extends E> sources) throws DecodeException {
		return sources.map((e) -> decode(e)).toList();
	}

	default <F> Decoder<F, D> fromDecoder(Decoder<F, E> decoder) {
		return new NestedDecoder<>(decoder, this);
	}

	default <T> Decoder<E, T> toDecoder(Decoder<D, T> decoder) {
		return new NestedDecoder<>(this, decoder);
	}

	public static <R> Decoder<R, R> identity() {
		return e -> e;
	}
}
