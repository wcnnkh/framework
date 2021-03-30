package scw.codec;

/**
 * 解码器<br/>
 * 为了兼容jdk1.5所以不使用default，请继承{@link AbstractDecoder}以实现默认方法
 * @author shuchaowen
 *
 * @param <E>
 * @param <D>
 */
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
		return new HierarchicalDecoder<F, E, D>(decoder, this);
	}
	
	/**
	 * decode -> decode -> decode ...<br/>
	 * @param decoder
	 * @return
	 */
	default <T> Decoder<E, T> toDecoder(Decoder<D, T> decoder){
		return new HierarchicalDecoder<E, D, T>(this, decoder);
	}
}
