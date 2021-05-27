package scw.codec;

/**
 * 多次解码器
 * @author shuchaowen
 *
 * @param <D>
 */
public interface MultipleDecoder<D> extends Decoder<D, D>{
	default int getCount() {
		return 1;
	}
	
	/**
	 * 第n次解码
	 * @param source
	 * @param currentCount
	 * @return
	 * @throws DecodeException
	 */
	default D decode(D source, int currentCount) throws DecodeException{
		return decode(source);
	}
	
	/**
	 * 多次操作
	 * @param count
	 * @return
	 */
	default MultipleDecoder<D> multiple(int count){
		return new MultipleDecoder<D>() {
			
			@Override
			public D decode(D source) throws DecodeException {
				D d = source;
				for(int i=0; i<count; i++) {
					d = decode(d, i);
				}
				return d;
			}
			
			@Override
			public D decode(D source, int currentCount) throws DecodeException {
				return MultipleDecoder.this.decode(source, currentCount);
			}
		};
	}
	
	static <D> MultipleDecoder<D> build(Decoder<D, D> decoder){
		return new MultipleDecoder<D>() {

			@Override
			public D decode(D source) throws DecodeException {
				return decoder.decode(source);
			}
		};
	}
}
