package scw.codec;

public class HierarchicalDecoder<P, E, D> implements Decoder<P, D>{
	protected final Decoder<P, E> parentDecoder;
	protected final Decoder<E, D> decoder;
	
	public HierarchicalDecoder(Decoder<P, E> parentDecoder, Decoder<E, D> decoder){
		this.parentDecoder = parentDecoder;
		this.decoder = decoder;
	}
	
	public Decoder<P, E> getParentDecoder() {
		return parentDecoder;
	}

	public Decoder<E, D> getDecoder() {
		return decoder;
	}

	public D decode(P source) throws DecodeException {
		E e = parentDecoder.decode(source);
		return decoder.decode(e);
	}

}
