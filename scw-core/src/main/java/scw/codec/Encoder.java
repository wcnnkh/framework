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
	/**
	 * 编码
	 * @param source
	 * @return
	 * @throws EncodeException
	 */
	E encode(D source) throws EncodeException;
	
	/**
	 * encode <- encode <- encode ...<br/>
	 * @param encoder
	 * @return
	 */
	default <F> Encoder<F, E> fromEncoder(Encoder<F, D> encoder){
		return new HierarchicalEncoder<F, D, E>(encoder, this);
	}
	
	/**
	 * encode -> encode -> encode ... <br/>
	 * @param encoder
	 * @return
	 */
	default <T> Encoder<D, T> toEncoder(Encoder<E, T> encoder){
		return new HierarchicalEncoder<D, E, T>(this, encoder);
	}
	
	default <T> Signer<D, T> to(Signer<E, T> signer){
		return new HierarchicalSigner<D, E, T>(this, signer);
	}
}