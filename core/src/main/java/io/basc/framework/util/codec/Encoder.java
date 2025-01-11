package io.basc.framework.util.codec;

import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.check.Validator;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.function.Function;
import lombok.NonNull;

@FunctionalInterface
public interface Encoder<D, E> extends Validator<D, E> {
	E encode(D source) throws EncodeException;

	@Override
	default boolean verify(D source, E encode) throws EncodeException {
		return ObjectUtils.equals(this.encode(source), encode);
	}

	default Elements<E> encodeAll(@NonNull Elements<? extends D> sources) throws EncodeException {
		return sources.map((e) -> encode(e)).toList();
	}

	default <F> Encoder<F, E> fromEncoder(Encoder<F, D> encoder) {
		return new NestedEncoder<>(encoder, this);
	}

	default <T> Encoder<D, T> toEncoder(Encoder<E, T> encoder) {
		return new NestedEncoder<>(this, encoder);
	}

	default Function<D, E, EncodeException> toEncodeProcessor() {
		return (o) -> encode(o);
	}

	public static <R> Encoder<R, R> identity() {
		return e -> e;
	}
}