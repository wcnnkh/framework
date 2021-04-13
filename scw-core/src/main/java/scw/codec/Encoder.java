package scw.codec;

import scw.convert.Converter;
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
	
	static class NestedEncoder<D, T, E> implements Encoder<D, E>{
		private final Encoder<D, T> parent;
		private final Encoder<T, E> encoder;
		
		public Encoder<D, T> getParent() {
			return parent;
		}

		public Encoder<T, E> getEncoder() {
			return encoder;
		}

		public NestedEncoder(Encoder<D, T> parent, Encoder<T, E> encoder) {
			this.parent = parent;
			this.encoder = encoder;
		}
		
		@Override
		public E encode(D source) throws EncodeException {
			return encoder.encode(parent.encode(source));
		}
	}
	
	/**
	 * encode <- encode <- encode ...<br/>
	 * @param encoder
	 * @return
	 */
	default <F> Encoder<F, E> fromEncoder(Encoder<F, D> encoder){
		return new NestedEncoder<F, D, E>(encoder, this);
	}
	
	/**
	 * encode -> encode -> encode ... <br/>
	 * @param encoder
	 * @return
	 */
	default <T> Encoder<D, T> toEncoder(Encoder<E, T> encoder){
		return new NestedEncoder<D, E, T>(this, encoder);
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
	
	default Converter<D, E> toEncodeConverter(){
		return new Converter<D, E>() {

			@Override
			public E convert(D o) {
				return encode(o);
			}
		};
	}
}