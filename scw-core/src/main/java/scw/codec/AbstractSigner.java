package scw.codec;

public abstract class AbstractSigner<D, E> extends AbstractEncoder<D, E>
		implements Signer<D, E> {

	public <F> Signer<F, E> fromEncoder(Encoder<F, D> encoder) {
		return new HierarchicalSigner<F, D, E>(encoder, this);
	}
	
	public <T> Signer<D, T> to(final Codec<E, T> codec) {
		return new AbstractSigner<D, T>() {

			public T encode(D source) throws EncodeException {
				E e = AbstractSigner.this.encode(source);
				return codec.encode(e);
			}

			public boolean verify(D source, T encode) throws CodecException {
				E e = codec.decode(encode);
				return AbstractSigner.this.verify(source, e);
			}
		};
	}
}
