package scw.codec;

import scw.core.utils.ObjectUtils;


/**
 * 编码器<br/>
 * @author shuchaowen
 *
 * @param <D>
 * @param <E>
 */
@FunctionalInterface
public interface Encoder<D, E>{
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
		return new Encoder<F, E>() {

			@Override
			public E encode(F source) throws EncodeException {
				return Encoder.this.encode(encoder.encode(source));
			}
		};
	}
	
	/**
	 * encode -> encode -> encode ... <br/>
	 * @param encoder
	 * @return
	 */
	default <T> Encoder<D, T> toEncoder(Encoder<E, T> encoder){
		return new Encoder<D, T>() {

			@Override
			public T encode(D source) throws EncodeException {
				return encoder.encode(Encoder.this.encode(source));
			}
		};
	}
	
	default Signer<D, E> toSigner(){
		return new Signer<D, E>() {

			@Override
			public E encode(D source) throws EncodeException {
				return Encoder.this.encode(source);
			}
			
			@Override
			public boolean verify(D source, E encode) {
				return ObjectUtils.nullSafeEquals(this.encode(source), encode);
			}
		};
	}
	
	default <T> Signer<D, T> toSigner(Signer<E, T> signer){
		return new Signer<D, T>() {

			@Override
			public boolean verify(D source, T encode) {
				return signer.verify(Encoder.this.encode(source), encode);
			}

			@Override
			public T encode(D source) throws EncodeException {
				return signer.encode(Encoder.this.encode(source));
			}
		};
	}
}