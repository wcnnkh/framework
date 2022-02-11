package io.basc.framework.codec;

class NestedEncodeSigner<P extends Encoder<D, T>, EN extends Signer<T, E>, D, T, E>
		extends NestedEncoder<P, EN, D, T, E> implements Signer<D, E> {

	public NestedEncodeSigner(P parent, EN encoder) {
		super(parent, encoder);
	}

	@Override
	public boolean verify(D source, E encode) throws CodecException {
		return encoder.verify(parent.encode(source), encode);
	}
}
