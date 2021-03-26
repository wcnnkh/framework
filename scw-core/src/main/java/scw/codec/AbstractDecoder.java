package scw.codec;



public abstract class AbstractDecoder<E, D> implements Decoder<E, D>{
	
	public <F> Decoder<F, D> fromDecoder(Decoder<F, E> decoder) {
		return new HierarchicalDecoder<F, E, D>(decoder, this);
	}

	public <T> Decoder<E, T> toDecoder(Decoder<D, T> decoder) {
		return new HierarchicalDecoder<E, D, T>(this, decoder);
	}
}
