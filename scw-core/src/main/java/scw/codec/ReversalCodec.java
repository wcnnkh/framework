package scw.codec;

/**
 * 将编解码的行为颠倒
 * @author shuchaowen
 *
 * @param <E>
 * @param <D>
 */
public class ReversalCodec<E, D> extends AbstractCodec<E, D>{
	private final Codec<D, E> codec;
	
	public ReversalCodec(Codec<D, E> codec){
		this.codec = codec;
	}
	
	public D encode(E source) throws EncodeException {
		try {
			return codec.decode(source);
		} catch (DecodeException e) {
			throw new EncodeException("reversal",  e);
		}
	}

	public E decode(D source) throws DecodeException {
		try {
			return codec.encode(source);
		} catch (EncodeException e) {
			throw new DecodeException("reversal", e);
		}
	}

	@Override
	public Codec<D, E> reversal() {
		return codec;
	}
}
