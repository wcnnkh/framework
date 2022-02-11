package io.basc.framework.codec;

import java.util.Collection;
import java.util.List;

import io.basc.framework.convert.Converter;
import io.basc.framework.util.Wrapper;

@SuppressWarnings("unchecked")
public class EncoderWrapper<W extends Encoder<D, E>, D, E> extends Wrapper<W> implements Encoder<D, E> {

	public EncoderWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public E encode(D source) throws EncodeException {
		return wrappedTarget.encode(source);
	}

	@Override
	public List<E> encode(Collection<? extends D> sources) throws EncodeException {
		return wrappedTarget.encode(sources);
	}

	@Override
	public E[] encode(D... sources) throws DecodeException {
		return wrappedTarget.encode(sources);
	}

	@Override
	public <F> Encoder<F, E> fromEncoder(Encoder<F, D> encoder) {
		return wrappedTarget.fromEncoder(encoder);
	}

	@Override
	public Converter<D, E> toEncodeConverter() {
		return wrappedTarget.toEncodeConverter();
	}

	@Override
	public <T> Encoder<D, T> toEncoder(Encoder<E, T> encoder) {
		return wrappedTarget.toEncoder(encoder);
	}

	@Override
	public Signer<D, E> toSigner() {
		return wrappedTarget.toSigner();
	}

	@Override
	public <T> Signer<D, T> toSigner(Signer<E, T> signer) {
		return wrappedTarget.toSigner(signer);
	}
}
