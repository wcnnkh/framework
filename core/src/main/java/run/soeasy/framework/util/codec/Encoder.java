package run.soeasy.framework.util.codec;

import java.util.function.BiPredicate;

import lombok.NonNull;
import run.soeasy.framework.util.ObjectUtils;
import run.soeasy.framework.util.collections.Elements;
import run.soeasy.framework.util.function.Wrapper;

@FunctionalInterface
public interface Encoder<D, E> extends BiPredicate<D, E> {

	public static interface EncoderWrapper<D, E, W extends Encoder<D, E>> extends Encoder<D, E>, Wrapper<W> {
		@Override
		default E encode(D source) throws EncodeException {
			return getSource().encode(source);
		}

		@Override
		default boolean test(D source, E encode) throws EncodeException {
			return getSource().test(source, encode);
		}

		@Override
		default <F> Encoder<F, E> fromEncoder(Encoder<F, D> encoder) {
			return getSource().fromEncoder(encoder);
		}

		@Override
		default <T> Encoder<D, T> toEncoder(Encoder<E, T> encoder) {
			return getSource().toEncoder(encoder);
		}

		@Override
		default Elements<E> encodeAll(@NonNull Elements<? extends D> sources) throws EncodeException {
			return getSource().encodeAll(sources);
		}
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