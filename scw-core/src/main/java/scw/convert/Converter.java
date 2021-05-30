package scw.convert;

import java.util.Collection;

import scw.codec.DecodeException;
import scw.codec.Decoder;
import scw.codec.EncodeException;
import scw.codec.Encoder;
import scw.lang.Ignore;

@Ignore
@FunctionalInterface
public interface Converter<S, T> {
	T convert(S o);
	
	default <E> Converter<S, E> to(Converter<T, E> converter){
		return new Converter<S, E>() {

			@Override
			public E convert(S o) {
				return converter.convert(Converter.this.convert(o));
			}
		};
	}
	
	default <F> Converter<F, T> from(Converter<F, S> converter){
		return new Converter<F, T>(){

			@Override
			public T convert(F o) {
				return Converter.this.convert(converter.convert(o));
			}
		};
	}
	
	default Encoder<S, T> toEncoder(){
		return new Encoder<S, T>() {

			@Override
			public T encode(S source) throws EncodeException {
				return convert(source);
			}
		};
	}
	
	default Decoder<S, T> toDecoder(){
		return new Decoder<S, T>() {

			@Override
			public T decode(S source) throws DecodeException {
				return convert(source);
			}
		};
	}
	
	default <SL extends Collection<S>, TL extends Collection<T>> TL convert(SL sourceList, TL targetList){
		if(sourceList == null){
			return targetList;
		}

		for(S source : sourceList){
			T target = convert(source);
			targetList.add(target);
		}
		return targetList;
	}
}
