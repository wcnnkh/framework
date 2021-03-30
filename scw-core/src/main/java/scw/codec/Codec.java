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
		return new HierarchicalCodec<F, D, E>(codec, this);
	}
	
	/**
	 * encode -> encode -> encode ... <br/>
	 * decode <- decode <- decode ... <br/>
	 * @param codec
	 * @return
	 */
	default <T> Codec<D, T> to(Codec<E, T> codec){
		return new HierarchicalCodec<D, E, T>(this, codec);
	}
	
	/**
	 * 将编解码的行为颠倒
	 * @see ReversalCodec
	 * @return
	 */
	default Codec<E, D> reversal(){
		return new ReversalCodec<E, D>(this);
	}
}
