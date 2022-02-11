package io.basc.framework.codec;

import java.util.Collection;
import java.util.List;

import io.basc.framework.convert.Converter;
import io.basc.framework.util.Wrapper;

@SuppressWarnings("unchecked")
public class DecoderWrapper<W extends Decoder<E, D>, E, D> extends Wrapper<W> implements Decoder<E, D> {

	public DecoderWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public D decode(E source) throws DecodeException {
		return wrappedTarget.decode(source);
	}

	@Override
	public List<D> decode(Collection<? extends E> sources) throws DecodeException {
		return wrappedTarget.decode(sources);
	}

	@Override
	public D[] decode(E... sources) throws DecodeException {
		return wrappedTarget.decode(sources);
	}

	@Override
	public <F> Decoder<F, D> fromDecoder(Decoder<F, E> decoder) {
		return wrappedTarget.fromDecoder(decoder);
	}

	@Override
	public Converter<E, D> toDecodeConverter() {
		return wrappedTarget.toDecodeConverter();
	}

	@Override
	public <T> Decoder<E, T> toDecoder(Decoder<D, T> decoder) {
		return wrappedTarget.toDecoder(decoder);
	}
}
