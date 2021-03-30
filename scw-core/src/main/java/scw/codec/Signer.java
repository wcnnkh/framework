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
	
	/**
	 * 签名
	 */
	E encode(D source) throws EncodeException;
	
	/**
	 * 验证签名
	 */
	boolean verify(D source, E encode) throws CodecException;
	
	default <F> Signer<F, E> fromEncoder(Encoder<F, D> encoder){
		return new HierarchicalSigner<F, D, E>(encoder, this);
	}
	
	default <T> Signer<D, T> to(Codec<E, T> codec){
		return new Signer<D, T>() {

			public T encode(D source) throws EncodeException {
				E e = Signer.this.encode(source);
				return codec.encode(e);
			}

			public boolean verify(D source, T encode) throws CodecException {
				E e = codec.decode(encode);
				return Signer.this.verify(source, e);
			}
		};
	}
}
