package scw.codec;

/**
 * 编解码器<br/>
 * @author shuchaowen
 * 
 * @param <D>
 * @param <E>
 */
public interface Codec<D, E> extends Encoder<D, E>, Decoder<E, D>{
	
	/**
	 * encode <- encode <- encode ...<br/>
	 * decode -> decode -> decode ...<br/>
	 * @param codec
	 * @return
	 */
	default <F> Codec<F, E> from(Codec<F, D> codec){
		return new Codec<F, E>() {

			@Override
			public E encode(F source) throws EncodeException {
				return Codec.this.encode(codec.encode(source));
			}

			@Override
			public F decode(E source) throws DecodeException {
				return codec.decode(Codec.this.decode(source));
			}
		};
	}
	
	/**
	 * encode -> encode -> encode ... <br/>
	 * decode <- decode <- decode ... <br/>
	 * @param codec
	 * @return
	 */
	default <T> Codec<D, T> to(Codec<E, T> codec){
		return new Codec<D, T>() {

			@Override
			public T encode(D source) throws EncodeException {
				return codec.encode(Codec.this.encode(source));
			}

			@Override
			public D decode(T source) throws DecodeException {
				return Codec.this.decode(codec.decode(source));
			}
		};
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
}
