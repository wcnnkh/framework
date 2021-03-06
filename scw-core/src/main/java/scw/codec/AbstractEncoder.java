package scw.codec;



public abstract class AbstractEncoder<D, E> implements Encoder<D, E>{

	public <F> Encoder<F, E> fromEncoder(Encoder<F, D> encoder) {
		return new HierarchicalEncoder<F, D, E>(encoder, this);
	}

	public <T> Encoder<D, T> toEncoder(Encoder<E, T> encoder) {
		return new HierarchicalEncoder<D, E, T>(this, encoder);
	}
	
	public <T> Signer<D, T> to(Signer<E, T> signer) {
		return new HierarchicalSigner<D, E, T>(this, signer);
	}
}
