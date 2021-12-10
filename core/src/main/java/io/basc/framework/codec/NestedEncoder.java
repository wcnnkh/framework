package io.basc.framework.codec;

final class NestedEncoder<D, T, E> implements Encoder<D, E> {
	private final Encoder<D, T> parent;
	private final Encoder<T, E> encoder;

	Encoder<D, T> getParent() {
		return parent;
	}

	public Encoder<T, E> getEncoder() {
		return encoder;
	}

	public NestedEncoder(Encoder<D, T> parent, Encoder<T, E> encoder) {
		this.parent = parent;
		this.encoder = encoder;
	}

	@Override
	public E encode(D source) throws EncodeException {
		return encoder.encode(parent.encode(source));
	}
}