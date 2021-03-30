package scw.codec;

public class HierarchicalSigner<P, D, E> implements Signer<P, E>{
	protected final Encoder<P, D> parentEncoder;
	protected final Signer<D, E> signer;
	
	public HierarchicalSigner(Encoder<P, D> parentEncoder, Signer<D, E> signer){
		this.parentEncoder = parentEncoder;
		this.signer = signer;
	}

	public Encoder<P, D> getParentEncoder() {
		return parentEncoder;
	}

	public Signer<D, E> getSigner() {
		return signer;
	}

	public E encode(P source) throws EncodeException {
		D d = parentEncoder.encode(source);
		return signer.encode(d);
	}

	public boolean verify(P source, E encode) throws CodecException {
		D d = parentEncoder.encode(source);
		return signer.verify(d, encode);
	}
}
