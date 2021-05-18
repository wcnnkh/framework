package scw.codec;

/**
 * 多次编码器
 * @author shuchaowen
 *
 * @param <E>
 */
public interface MultipleEncoder<E> extends Encoder<E, E> {

	default int getCount() {
		return 1;
	}
	
	/**
	 * 第n次编码行为
	 * @param encode
	 * @param currentCount 当前次数
	 * @return
	 * @throws EncodeException
	 */
	default E encode(E encode, int currentCount) throws EncodeException{
		return encode(encode);
	}
	
	/**
	 * 多次操作
	 * @param count
	 * @return
	 */
	default MultipleEncoder<E> multiple(int count){
		return new MultipleEncoder<E>() {

			@Override
			public E encode(E source) throws EncodeException {
				E e = source;
				for(int i=0; i<count; i++) {
					e = encode(e, i);
				}
				return e;
			}
			
			@Override
			public E encode(E encode, int currentCount) throws EncodeException{
				return MultipleEncoder.this.encode(encode, currentCount);
			}
			
			@Override
			public int getCount() {
				return count;
			}
		};
	}
	
	static <E> MultipleEncoder<E> build(Encoder<E, E> encoder){
		return new MultipleEncoder<E>() {

			@Override
			public E encode(E source) throws EncodeException {
				return encoder.encode(source);
			}
		};
	}
}
