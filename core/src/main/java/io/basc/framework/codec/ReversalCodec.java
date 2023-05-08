package io.basc.framework.codec;

final class ReversalCodec<E, D> implements Codec<E, D> {
	private final Codec<D, E> codec;

	ReversalCodec(Codec<D, E> codec) {
		this.codec = codec;
	}

	@Override
	public Codec<D, E> reverse() {
		return codec;
	}

	@Override
	public D encode(E source) throws EncodeException {
		try {
			return codec.decode(source);
		} catch (DecodeException e) {
			throw new EncodeException("reversal", e);
		}
	}

	@Override
	public E decode(D source) throws DecodeException {
		try {
			return codec.encode(source);
		} catch (EncodeException e) {
			throw new DecodeException("reversal", e);
		}
	}
}