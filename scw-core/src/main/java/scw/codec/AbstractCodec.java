package scw.codec;

public abstract class AbstractCodec<D, E> extends AbstractEncoder<D, E> implements Codec<D, E> {
	
	public <F> Decoder<F, D> fromDecoder(Decoder<F, E> decoder) {
		return new HierarchicalDecoder<F, E, D>(decoder, this);
	}

	public <T> Decoder<E, T> toDecoder(Decoder<D, T> decoder) {
		return new HierarchicalDecoder<E, D, T>(this, decoder);
	}
	
	public <F> Codec<F, E> from(Codec<F, D> codec) {
		return new HierarchicalCodec<F, D, E>(codec, this);
	}

	public <T> Codec<D, T> to(Codec<E, T> codec) {
		return new HierarchicalCodec<D, E, T>(this, codec);
	}
	
	/**
	 * 将编解码的行为颠倒
	 * 
	 * @return
	 */
	public Codec<E, D> reversal() {
		return new ReversalCodec<E, D>(this);
	}
}
