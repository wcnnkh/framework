package scw.codec;

/**
 * 编解码器<br/>
 * @author shuchaowen
 * 
 * @param <D>
 * @param <E>
 */
public interface Codec<D, E> extends Encoder<D, E>, Decoder<E, D>{
	
	static class NestedCodec<D, T, E> implements Codec<D, E>{
		private final Codec<D, T> parent;
		private final Codec<T, E> codec;
		
		public NestedCodec(Codec<D, T> parent, Codec<T, E> codec) {
			this.parent = parent;
			this.codec = codec;
		}

		@Override
		public E encode(D source) throws EncodeException {
			return codec.encode(parent.encode(source));
		}

		@Override
		public D decode(E source) throws DecodeException {
			return parent.decode(codec.decode(source));
		}
		
	}
	
	/**
	 * encode <- encode <- encode ...<br/>
	 * decode -> decode -> decode ...<br/>
	 * @param codec
	 * @return
	 */
	default <F> Codec<F, E> from(Codec<F, D> codec){
		return new NestedCodec<F, D, E>(codec, this);
	}
	
	/**
	 * encode -> encode -> encode ... <br/>
	 * decode <- decode <- decode ... <br/>
	 * @param codec
	 * @return
	 */
	default <T> Codec<D, T> to(Codec<E, T> codec){
		return new NestedCodec<D, E, T>(this, codec);
	}
	
	/**
	 * 将编解码的行为颠倒
	 * @return
	 */
	default Codec<E, D> reversal(){
		return new Codec<E, D>() {

			@Override
			public D encode(E source) throws EncodeException {
				try {
					return Codec.this.decode(source);
				} catch (DecodeException e) {
					throw new EncodeException("reversal",  e);
				}
			}

			@Override
			public E decode(D source) throws DecodeException {
				try {
					return Codec.this.encode(source);
				} catch (EncodeException e) {
					throw new DecodeException("reversal", e);
				}
			}
		};
	}
	
	static <D, E> Codec<D, E> construct(Encoder<D, E> encoder, Decoder<E, D> decoder){
		return new Codec<D, E>() {

			@Override
			public E encode(D source) throws EncodeException {
				return encoder.encode(source);
			}

			@Override
			public D decode(E source) throws DecodeException {
				return decoder.decode(source);
			}
		};
	}
}
