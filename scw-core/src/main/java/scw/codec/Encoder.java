package scw.codec;

/**
 * 编码器<br/>
 * 为了兼容jdk1.5所以不使用default，请继承{@link AbstractEncoder}以实现默认方法<br/>
 * @author shuchaowen
 *
 * @param <D>
 * @param <E>
 */
public interface Encoder<D, E> {
	E encode(D source) throws EncodeException;
	
	/**
	 * encode <- encode <- encode ...<br/>
	 * @param encoder
	 * @return
	 */
	<F> Encoder<F, E> fromEncoder(Encoder<F, D> encoder);
	
	/**
	 * encode -> encode -> encode ... <br/>
	 * @param encoder
	 * @return
	 */
	<T> Encoder<D, T> toEncoder(Encoder<E, T> encoder);
	
	<T> Signer<D, T> to(Signer<E, T> signer);
}