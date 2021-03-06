package scw.codec;

public abstract class AbstractCodec<D, E> implements Codec<D, E> {
	
	public <F> AbstractCodec<F, E> from(Codec<F, D> codec) {
		return new HierarchicalCodec<F, D, E>(codec, this);
	}

	public <T> AbstractCodec<D, T> to(Codec<E, T> codec) {
		return new HierarchicalCodec<D, E, T>(this, codec);
	}
	
	/**
	 * 将编解码的行为颠倒
	 * @return
	 */
	public AbstractCodec<E, D> reversal(){
		return new AbstractCodec<E, D>(){

			public D encode(E source) throws EncodeException {
				return AbstractCodec.this.decode(source);
			}

			public E decode(D source) throws DecodeException {
				return AbstractCodec.this.encode(source);
			}
			
		};
	}
}
