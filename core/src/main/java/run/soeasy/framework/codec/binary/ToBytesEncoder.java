package run.soeasy.framework.codec.binary;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.Encoder;
import run.soeasy.framework.codec.lang.Base64;
import run.soeasy.framework.codec.lang.HexCodec;
import run.soeasy.framework.codec.security.MD5;
import run.soeasy.framework.codec.security.SHA1;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.FileUtils;
import run.soeasy.framework.io.IOUtils;

public interface ToBytesEncoder<D> extends Encoder<D, byte[]> {

	default Encoder<D, String> toBase64() {
		return toEncoder(Base64.DEFAULT);
	}

	default Encoder<D, String> toHex() {
		return toEncoder(HexCodec.DEFAULT);
	}

	default Encoder<D, String> toMD5() {
		return toEncoder(MD5.DEFAULT);
	}

	default Encoder<D, String> toSHA1() {
		return toEncoder(SHA1.DEFAULT);
	}

	@Override
	default byte[] encode(D source) throws EncodeException {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try {
			encode(source, target);
		} catch (IOException e) {
			throw new EncodeException(e);
		}
		return target.toByteArray();
	}

	default void encode(D source, File target) throws IOException, EncodeException {
		FileOutputStream fos = new FileOutputStream(target);
		try {
			encode(source, fos);
		} finally {
			IOUtils.close(fos);
		}
	}

	void encode(D source, OutputStream target) throws IOException, EncodeException;

	default <E extends Throwable> void encode(D source, BufferConsumer<? super byte[], ? extends E> targetConsumer)
			throws IOException, EncodeException, E {
		File tempFile = File.createTempFile("encode", "processor");
		try {
			encode(source, tempFile);
			FileUtils.read(tempFile, targetConsumer);
		} finally {
			tempFile.delete();
		}
	}
}
