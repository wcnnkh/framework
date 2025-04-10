package run.soeasy.framework.util.codec.binary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import run.soeasy.framework.util.codec.DecodeException;
import run.soeasy.framework.util.codec.EncodeException;
import run.soeasy.framework.util.codec.NestedCodec;

class NestedBytesCodec extends NestedCodec<BytesCodec, BytesCodec, byte[], byte[], byte[]> implements BytesCodec {
	private final int count;

	public NestedBytesCodec(BytesCodec parent, BytesCodec codec) {
		this(parent, codec, 1);
	}

	public NestedBytesCodec(BytesCodec parent, BytesCodec codec, int count) {
		super(parent, codec);
		this.count = count;
	}

	@Override
	public void encode(InputStream source, int bufferSize, OutputStream target) throws IOException, EncodeException {
		if (parent == null) {
			codec.encode(source, bufferSize, target, count);
			return;
		}
		File tempFile = File.createTempFile("nested", "decode");
		try {
			parent.encode(source, bufferSize, tempFile, count);
			codec.encode(tempFile, bufferSize, target, count);
		} finally {
			tempFile.delete();
		}

	}

	@Override
	public void decode(InputStream source, int bufferSize, OutputStream target) throws DecodeException, IOException {
		if (parent == null) {
			codec.decode(source, bufferSize, target, count);
			return;
		}

		File tempFile = File.createTempFile("nested", "decode");
		try {
			codec.decode(source, bufferSize, tempFile, count);
			parent.decode(tempFile, bufferSize, target, count);
		} finally {
			tempFile.delete();
		}

	}
}
