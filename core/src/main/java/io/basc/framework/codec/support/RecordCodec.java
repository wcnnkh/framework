package io.basc.framework.codec.support;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.io.Bits;
import io.basc.framework.util.Assert;

public final class RecordCodec<D> implements ToBytesCodec<D> {
	private final Codec<D, byte[]> codec;

	public RecordCodec(Codec<D, byte[]> codec) {
		Assert.requiredArgument(codec != null, "codec");
		this.codec = codec;
	}

	@Override
	public void encode(D source, OutputStream target) throws IOException, EncodeException {
		if (source == null) {
			return;
		}

		byte[] value = codec.encode(source);
		Bits.writeInt(value == null ? 0 : value.length, target);
		if (value != null && value.length != 0) {
			target.write(value);
		}
	}

	@Override
	public D decode(InputStream source, int bufferSize) throws IOException, DecodeException, EOFException {
		int size = Bits.readInt(source);
		if (size == 0) {
			return null;
		}

		byte[] buff = new byte[size];
		if (source.read(buff) == -1) {
			throw new EOFException();
		}
		return codec.decode(buff);
	}

}
