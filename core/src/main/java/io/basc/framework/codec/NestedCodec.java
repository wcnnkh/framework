package io.basc.framework.codec;

final class NestedCodec<D, T, E> implements Codec<D, E> {
	private final Codec<D, T> parent;
	private final Codec<T, E> codec;

	NestedCodec(Codec<D, T> parent, Codec<T, E> codec) {
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
}