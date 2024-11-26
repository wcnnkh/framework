package io.basc.framework.util.codec;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.util.Pipeline;
import io.basc.framework.util.collect.CollectionUtils;

@FunctionalInterface
public interface Decoder<E, D> {
	D decode(E source) throws DecodeException;

	default List<D> decodeAll(Collection<? extends E> sources) throws DecodeException {
		if (CollectionUtils.isEmpty(sources)) {
			return Collections.emptyList();
		}

		return sources.stream().map((e) -> decode(e)).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	default D[] decodeAll(E... sources) throws DecodeException {
		return toDecodeProcessor().processAll(sources);
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
