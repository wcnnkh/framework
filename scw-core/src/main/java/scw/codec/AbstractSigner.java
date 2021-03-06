package scw.codec;



public abstract class AbstractSigner<D, E> extends AbstractEncoder<D, E>
		implements Signer<D, E> {

	public <F> AbstractEncoder<F, E> from(Encoder<F, D> encoder) {
		return new HierarchicalSigner<F, D, E>(encoder, this);
	}
}
