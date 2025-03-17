package run.soeasy.framework.util.codec;

public class NestedEncoder<P extends Encoder<D, T>, EN extends Encoder<T, E>, D, T, E> implements Encoder<D, E> {
	protected final P parent;
	protected final EN encoder;

	public NestedEncoder(P parent, EN encoder) {
		this.parent = parent;
		this.encoder = encoder;
	}

	@Override
	public E encode(D source) throws EncodeException {
		return encoder.encode(parent.encode(source));
	}

	@Override
	public boolean test(D source, E encode) throws EncodeException {
		return encoder.test(parent.encode(source), encode);
	}
}