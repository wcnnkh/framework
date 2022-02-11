package io.basc.framework.codec.decode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.codec.DecodeException;

public class NestedBytesDecoder implements BytesDecoder {
	private final BytesDecoder parent;
	private final BytesDecoder decoder;

	public NestedBytesDecoder(BytesDecoder parent, BytesDecoder decoder) {
		this.parent = parent;
		this.decoder = decoder;
	}

	@Override
	public byte[] decode(InputStream source, int bufferSize, int count) throws IOException, DecodeException {
		return decoder.decode(parent.decode(source, bufferSize, count));
	}
}
