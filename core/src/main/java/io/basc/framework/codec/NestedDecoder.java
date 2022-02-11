package io.basc.framework.codec;

public class NestedDecoder<P extends Decoder<D, T>, DE extends Decoder<T, E>, D, T, E> implements Decoder<D, E> {
	protected final P parent;
	protected final DE decoder;

	public NestedDecoder(P parent, DE decoder) {
		this.parent = parent;
		this.decoder = decoder;
	}

	@Override
	public E decode(D source) throws DecodeException {
		return decoder.decode(parent.decode(source));
	}
}