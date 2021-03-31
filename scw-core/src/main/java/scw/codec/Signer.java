package scw.codec;

import scw.util.Validator;

/**
 * 签名器<br/>
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
		return new Signer<F, E>() {

			@Override
			public E encode(F source) throws EncodeException {
				return Signer.this.encode(encoder.encode(source));
			}

			@Override
			public boolean verify(F source, E encode) throws CodecException {
				return Signer.this.verify(encoder.encode(source), encode);
			}
		};
	}
	
	default <T> Signer<D, T> to(Codec<E, T> codec){
		return new Signer<D, T>() {

			public T encode(D source) throws EncodeException {
				return codec.encode(Signer.this.encode(source));
			}

			public boolean verify(D source, T encode) throws CodecException {
				return Signer.this.verify(source, codec.decode(encode));
			}
		};
	}
}
