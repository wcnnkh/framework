package scw.codec;



public abstract class AbstractDecoder<E, D> implements Decoder<E, D>{
	
	public <F> AbstractDecoder<F, D> from(Decoder<F, E> decoder) {
		return new HierarchicalDecoder<F, E, D>(decoder, this);
	}

	public <T> AbstractDecoder<E, T> to(Decoder<D, T> decoder) {
		return new HierarchicalDecoder<E, D, T>(this, decoder);
	}
}
