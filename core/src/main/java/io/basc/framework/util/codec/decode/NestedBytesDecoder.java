package io.basc.framework.util.codec.decode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.util.codec.DecodeException;

public class NestedBytesDecoder implements BytesDecoder {
	private final BytesDecoder parent;
	private final BytesDecoder decoder;
	private final int count;

	public NestedBytesDecoder(BytesDecoder parent, BytesDecoder decoder) {
		this(parent, decoder, 1);
	}

	public NestedBytesDecoder(BytesDecoder parent, BytesDecoder decoder, int count) {
		this.parent = parent;
		this.decoder = decoder;
		this.count = count;
	}

	@Override
	public void decode(InputStream source, int bufferSize, OutputStream target) throws DecodeException, IOException {
		File tempFile = File.createTempFile("nested", "decode");
		try {
			parent.decode(source, bufferSize, tempFile, count);
			decoder.decode(tempFile, bufferSize, target, count);
		} finally {
			tempFile.delete();
		}
	}
}
