package io.basc.framework.codec;

final class NestedDecoder<D, T, E> implements Decoder<D, E> {
	private final Decoder<D, T> parent;
	private final Decoder<T, E> decoder;

	NestedDecoder(Decoder<D, T> parent, Decoder<T, E> decoder) {
		this.parent = parent;
		this.decoder = decoder;
	}

	public Decoder<D, T> getParent() {
		return parent;
	}

	public Decoder<T, E> getDecoder() {
		return decoder;
	}

	@Override
	public E decode(D source) throws DecodeException {
		return decoder.decode(parent.decode(source));
	}
}