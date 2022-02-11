package io.basc.framework.codec;

import java.io.IOException;
import java.io.InputStream;

public class NestedBytesCodec extends NestedCodec<BytesCodec, BytesCodec, byte[], byte[], byte[]>
		implements BytesCodec {

	public NestedBytesCodec(BytesCodec parent, BytesCodec codec) {
		super(parent, codec);
	}

	@Override
	public byte[] decode(InputStream source) throws IOException, DecodeException {
		byte[] value = codec.decode(source);
		return parent.decode(value);
	}

	@Override
	public byte[] encode(InputStream source) throws IOException, DecodeException {
		byte[] value = parent.encode(source);
		return codec.encode(value);
	}

	@Override
	public byte[] encode(byte[] encode, int count) throws EncodeException {
		byte[] value = parent.encode(encode, count);
		return codec.encode(value);
	}

	@Override
	public byte[] decode(byte[] source, int count) throws DecodeException {
		byte[] value = codec.decode(source, count);
		return parent.decode(value);
	}

	@Override
	public byte[] decode(InputStream source, int bufferSize) throws IOException, DecodeException {
		byte[] value = codec.decode(source, bufferSize);
		return parent.decode(value);
	}

	@Override
	public byte[] encode(InputStream source, int bufferSize) throws IOException, DecodeException {
		byte[] value = parent.encode(source, bufferSize);
		return codec.encode(value);
	}
}
