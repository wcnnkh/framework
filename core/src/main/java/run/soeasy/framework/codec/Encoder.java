package run.soeasy.framework.codec;

import java.util.function.BiPredicate;

import lombok.NonNull;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.Elements;

@FunctionalInterface
public interface Encoder<D, E> extends BiPredicate<D, E> {
	default boolean canEncode() {
		return true;
	}

	E encode(D source) throws EncodeException;

	@Override
	default boolean test(D source, E encode) throws EncodeException {
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

	public static <R> Encoder<R, R> identity() {
		return e -> e;
	}
}