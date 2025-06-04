package run.soeasy.framework.codec;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.Wrapper;

public interface EncoderWrapper<D, E, W extends Encoder<D, E>> extends Encoder<D, E>, Wrapper<W> {
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

	@Override
	default boolean canEncode() {
		return getSource().canEncode();
	}
}