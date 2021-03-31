package scw.codec;

/**
 * 解码器<br/>
 * @author shuchaowen
 *
 * @param <E>
 * @param <D>
 */
@FunctionalInterface
public interface Decoder<E, D> {
	/**
	 * 解码
	 * @param source
	 * @return
	 * @throws DecodeException
	 */
	D decode(E source) throws DecodeException;
	
	/**
	 * decode <- decode <- decode ... <br/>
	 * @param decoder
	 * @return
	 */
	default <F> Decoder<F, D> fromDecoder(Decoder<F, E> decoder){
		return new Decoder<F, D>() {

			@Override
			public D decode(F source) throws DecodeException {
				return Decoder.this.decode(decoder.decode(source));
			}
		};
	}
	
	/**
	 * decode -> decode -> decode ...<br/>
	 * @param decoder
	 * @return
	 */
	default <T> Decoder<E, T> toDecoder(Decoder<D, T> decoder){
		return new Decoder<E, T>(){
			@Override
			public T decode(E source) throws DecodeException {
				return decoder.decode(Decoder.this.decode(source));
			}
		};
	}
}
