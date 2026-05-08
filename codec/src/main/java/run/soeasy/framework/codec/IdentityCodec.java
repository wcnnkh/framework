package run.soeasy.framework.codec;

final class IdentityCodec<T> implements Codec<T, T> {
	static final IdentityCodec<?> INSTANCE = new IdentityCodec<>();

	@Override
	public T encode(T source) throws CodecException {
		return source;
	}

	@Override
	public T decode(T source) throws CodecException {
		return source;
	}

}
