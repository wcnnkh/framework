package scw.codec.multiple;

import scw.codec.Codec;
import scw.codec.DecodeException;
import scw.codec.EncodeException;

/**
 * 多次编解码
 * @author shuchaowen
 *
 * @param <T>
 */
public interface MultipleCodec<T> extends Codec<T, T>, MultipleEncoder<T>, MultipleDecoder<T>{

	@Override
	default int getCount() {
		return 1;
	}

	/**
	 * 多次操作
	 */
	@Override
	default MultipleCodec<T> multiple(int count) {
		return new MultipleCodec<T>() {
			
			@Override
			public T decode(T source) throws DecodeException {
				T t = source;
				for(int i=0; i<count; i++) {
					t = decode(t, i);
				}
				return t;
			}
			
			@Override
			public T decode(T source, int currentCount) throws DecodeException{
				return MultipleCodec.this.decode(source, currentCount);
			}

			@Override
			public T encode(T source) throws EncodeException {
				T t = source;
				for(int i=0; i<count; i++) {
					t = encode(t, i);
				}
				return t;
			}
			
			@Override
			public T encode(T encode, int currentCount) throws EncodeException{
				return MultipleCodec.this.encode(encode, currentCount);
			}
			
			@Override
			public int getCount() {
				return count;
			}
		};
	}
	
	static <T> MultipleCodec<T> construct(Codec<T, T> codec){
		return new MultipleCodec<T>() {

			@Override
			public T encode(T source) throws EncodeException {
				return codec.encode(source);
			}

			@Override
			public T decode(T source) throws DecodeException {
				return codec.decode(source);
			}
		};
	}
	
}
