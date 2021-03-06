package scw.codec;

import scw.util.Validator;

/**
 * 签名器<br/>
 * 为了兼容jdk1.5所以不使用default，请实现{@link AbstractSigner}以实现默认方法<br/>
 * @author asus1
 *
 * @param <D>
 * @param <E>
 */
public interface Signer<D, E> extends Encoder<D, E>, Validator<D, E> {
	
	<F> Signer<F, E> fromEncoder(Encoder<F, D> encoder);
	
	<T> Signer<D, T> to(final Codec<E, T> codec);
}
