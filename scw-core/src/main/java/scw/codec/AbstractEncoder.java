package scw.codec;



public abstract class AbstractEncoder<D, E> implements Encoder<D, E> {

	public <F> AbstractEncoder<F, E> from(Encoder<F, D> encoder) {
		return new HierarchicalEncoder<F, D, E>(encoder, this);
	}

	public <T> AbstractEncoder<D, T> to(Encoder<E, T> encoder) {
		return new HierarchicalEncoder<D, E, T>(this, encoder);
	}
}
