package scw.codec;

public class HierarchicalEncoder<P, D, E> extends AbstractEncoder<P, E>{
	protected final Encoder<P, D> parentEncoder;
	protected final Encoder<D, E> encoder;
	
	public HierarchicalEncoder(Encoder<P, D> parentEncoder, Encoder<D, E> encoder){
		this.parentEncoder = parentEncoder;
		this.encoder = encoder;
	}

	public Encoder<P, D> getParentEncoder() {
		return parentEncoder;
	}

	public Encoder<D, E> getEncoder() {
		return encoder;
	}

	public E encode(P source) throws EncodeException {
		D d = parentEncoder.encode(source);
		return encoder.encode(d);
	}
}
