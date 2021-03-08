package scw.codec;

import scw.util.Validator;

/**
 * 签名器<br/>
 * 为了兼容jdk1.5所以不使用default，请继承{@link AbstractSigner}以实现默认方法<br/>
 * @author shuchaowen
 *
 * @param <D>
 * @param <E>
 */
public interface Signer<D, E> extends Encoder<D, E>, Validator<D, E> {
	
	boolean verify(D source, E encode) throws CodecException;
	
	<F> Signer<F, E> fromEncoder(Encoder<F, D> encoder);
	
	<T> Signer<D, T> to(Codec<E, T> codec);
}
