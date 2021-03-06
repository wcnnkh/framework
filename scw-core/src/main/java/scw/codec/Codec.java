package scw.codec;

/**
 * 编解码器<br/>
 * 为了兼容jdk1.5所以不使用default，请实现{@link AbstractCodec}以实现默认方法
 * @author shuchaowen
 * 
 * @see AbstractCodec
 *
 * @param <D>
 * @param <E>
 */
public interface Codec<D, E> extends Encoder<D, E>, Decoder<E, D>{
	
	<F> Codec<F, E> from(Codec<F, D> codec);
	
	<T> Codec<D, T> to(Codec<E, T> codec);
	
	/**
	 * 将编解码的行为颠倒
	 * @see ReversalCodec
	 * @return
	 */
	Codec<E, D> reversal();
}
