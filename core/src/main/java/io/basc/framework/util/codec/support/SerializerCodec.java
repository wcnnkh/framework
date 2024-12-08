package io.basc.framework.util.codec.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.util.codec.DecodeException;
import io.basc.framework.util.codec.EncodeException;
import io.basc.framework.util.io.serializer.Serializer;

public class SerializerCodec<T> implements ToBytesCodec<T> {

	private final Serializer serializer;

	public SerializerCodec(Serializer serializer) {
		this.serializer = serializer;
	}

	public byte[] encode(T source) throws EncodeException {
		return serializer.serialize(source);
	}

	@Override
	public T decode(InputStream source, int bufferSize) throws IOException, DecodeException {
		try {
			return serializer.deserialize(source);
		} catch (ClassNotFoundException e) {
			throw new DecodeException(e);
		}
	}

	@Override
	public void encode(T source, OutputStream target) throws IOException, EncodeException {
		serializer.serialize(source, target);
		;
	}
}
