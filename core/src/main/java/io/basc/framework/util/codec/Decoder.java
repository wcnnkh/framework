package io.basc.framework.util.codec;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Pipeline;
import lombok.NonNull;

@FunctionalInterface
public interface Decoder<E, D> {
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

	default Pipeline<E, D, DecodeException> toDecodeProcessor() {
		return (o) -> decode(o);
	}

	public static <R> Decoder<R, R> identity() {
		return e -> e;
	}
}
