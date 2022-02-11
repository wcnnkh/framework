package io.basc.framework.codec;

class CodedSigner<D, T, E> implements Signer<D, E> {
	protected final Codec<T, E> codec;
	protected final Signer<D, T> signer;

	public CodedSigner(Codec<T, E> codec, Signer<D, T> signer) {
		this.codec = codec;
		this.signer = signer;
	}

	@Override
	public E encode(D source) throws EncodeException {
		return codec.encode(signer.encode(source));
	}

	@Override
	public boolean verify(D source, E encode) throws CodecException {
		return signer.verify(source, codec.decode(encode));
	}
}
