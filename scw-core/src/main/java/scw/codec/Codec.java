package scw.codec;

/**
 * 编解码器<br/>
 * 为了兼容jdk1.5所以不使用default，请继承{@link AbstractCodec}以实现默认方法
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
	<F> Codec<F, E> from(Codec<F, D> codec);
	
	/**
	 * encode -> encode -> encode ... <br/>
	 * decode <- decode <- decode ... <br/>
	 * @param codec
	 * @return
	 */
	<T> Codec<D, T> to(Codec<E, T> codec);
	
	/**
	 * 将编解码的行为颠倒
	 * @see ReversalCodec
	 * @return
	 */
	Codec<E, D> reversal();
}
