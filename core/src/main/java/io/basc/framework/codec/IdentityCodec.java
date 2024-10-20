package io.basc.framework.codec;

public final class IdentityCodec<T> implements Codec<T, T> {

	@Override
	public T encode(T source) throws EncodeException {
		return source;
	}

	@Override
	public T decode(T source) throws DecodeException {
		return source;
	}

}
