package io.basc.framework.util.codec;

public class NestedCodec<P extends Codec<D, T>, C extends Codec<T, E>, D, T, E> implements Codec<D, E> {
	protected final P parent;
	protected final C codec;

	public NestedCodec(P parent, C codec) {
		this.parent = parent;
		this.codec = codec;
	}

	@Override
	public E encode(D source) throws EncodeException {
		return codec.encode(parent.encode(source));
	}

	@Override
	public D decode(E source) throws DecodeException {
		return parent.decode(codec.decode(source));
	}

	@Override
	public boolean test(D source, E encode) throws EncodeException {
		return codec.test(parent.encode(source), encode);
	}
}