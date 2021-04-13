package scw.codec;

import scw.convert.Converter;

/**
 * 解码器<br/>
 * @author shuchaowen
 *
 * @param <E>
 * @param <D>
 */
@FunctionalInterface
public interface Decoder<E, D> {
	/**
	 * 解码
	 * @param source
	 * @return
	 * @throws DecodeException
	 */
	D decode(E source) throws DecodeException;
	
	static class NestedDecoder<D, T, E> implements Decoder<D, E>{
		private final Decoder<D, T> parent;
		private final Decoder<T, E> decoder;
		
		public NestedDecoder(Decoder<D, T> parent, Decoder<T, E> decoder) {
			this.parent = parent;
			this.decoder = decoder;
		}
		
		public Decoder<D, T> getParent() {
			return parent;
		}

		public Decoder<T, E> getDecoder() {
			return decoder;
		}

		@Override
		public E decode(D source) throws DecodeException {
			return decoder.decode(parent.decode(source));
		}
	}
	
	/**
	 * decode <- decode <- decode ... <br/>
	 * @param decoder
	 * @return
	 */
	default <F> Decoder<F, D> fromDecoder(Decoder<F, E> decoder){
		return new NestedDecoder<F, E, D>(decoder, this);
	}
	
	/**
	 * decode -> decode -> decode ...<br/>
	 * @param decoder
	 * @return
	 */
	default <T> Decoder<E, T> toDecoder(Decoder<D, T> decoder){
		return new NestedDecoder<E, D, T>(this, decoder);
	}
	
	default Converter<E, D> toDecodeConverter(){
		return new Converter<E, D>() {

			@Override
			public D convert(E o) {
				return decode(o);
			}
		};
	}
}
