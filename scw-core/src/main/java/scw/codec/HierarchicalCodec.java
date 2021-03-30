package scw.codec;


public class HierarchicalCodec<P, D, E> implements Codec<P, E> {
	protected final Codec<P, D> parentCodec;
	protected final Codec<D, E> codec;

	public HierarchicalCodec(Codec<P, D> parentCodec, Codec<D, E> codec) {
		this.parentCodec = parentCodec;
		this.codec = codec;
	}

	public Codec<P, D> getParentCodec() {
		return parentCodec;
	}

	public Codec<D, E> getCodec() {
		return codec;
	}

	public E encode(P source) throws EncodeException {
		D d = parentCodec.encode(source);
		return codec.encode(d);
	}

	public P decode(E source) throws DecodeException {
		D d = codec.decode(source);
		return parentCodec.decode(d);
	}

}
