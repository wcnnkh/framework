package io.basc.framework.codec;

public class NestedEncoder<P extends Encoder<D, T>, EN extends Encoder<T, E>, D, T, E> implements Encoder<D, E> {
	protected final P parent;
	protected final EN encoder;

	public NestedEncoder(P parent, EN encoder) {
		this.parent = parent;
		this.encoder = encoder;
	}

	@Override
	public E encode(D source) throws EncodeException {
		return encoder.encode(parent.encode(source));
	}

	@Override
	public boolean verify(D source, E encode) throws EncodeException {
		return encoder.verify(parent.encode(source), encode);
	}
}