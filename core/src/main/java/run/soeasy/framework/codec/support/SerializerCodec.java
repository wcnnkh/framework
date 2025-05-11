package run.soeasy.framework.codec.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.binary.ToBytesCodec;
import run.soeasy.framework.serializer.Serializer;

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
