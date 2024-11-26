package io.basc.framework.util.codec;

public final class IdentityCodec<T> implements Codec<T, T> {
	public static final IdentityCodec<?> INSTANCE = new IdentityCodec<>();

	@Override
	public T encode(T source) throws EncodeException {
		return source;
	}

	@Override
	public T decode(T source) throws DecodeException {
		return source;
	}

}
