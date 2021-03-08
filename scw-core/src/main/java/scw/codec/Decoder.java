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
	D decode(E source) throws DecodeException;
	
	/**
	 * decode <- decode <- decode ... <br/>
	 * @param decoder
	 * @return
	 */
	<F> Decoder<F, D> fromDecoder(Decoder<F, E> decoder);
	
	/**
	 * decode -> decode -> decode ...<br/>
	 * @param decoder
	 * @return
	 */
	<T> Decoder<E, T> toDecoder(Decoder<D, T> decoder);
}
