package run.soeasy.framework.codec.binary;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.Encoder;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.io.IOUtils;
import run.soeasy.framework.core.io.Resource;

public interface FromBytesEncoder<E> extends Encoder<byte[], E> {

	/**
	 * 对一个输入流的内容进行编码
	 * 
	 * @param source
	 * @param bufferSize
	 * @return
	 * @throws IOException
	 * @throws EncodeException
	 */
	E encode(InputStream source, int bufferSize) throws IOException, EncodeException;

	default boolean verify(InputStream source, E target) throws EncodeException, IOException {
		return verify(source, IOUtils.DEFAULT_BUFFER_SIZE, target);
	}

	default boolean verify(InputStream source, int bufferSize, E target) throws EncodeException, IOException {
		return ObjectUtils.equals(encode(source, bufferSize), target);
	}

	default boolean verify(File source, int bufferSize, E target) throws EncodeException, IOException {
		if (!source.exists()) {
			return false;
		}

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(source);
			return ObjectUtils.equals(encode(fis, bufferSize), target);
		} finally {
			IOUtils.close(fis);
		}
	}

	default boolean verify(File source, E target) throws EncodeException, IOException {
		return verify(source, IOUtils.DEFAULT_BUFFER_SIZE, target);
	}

	default boolean verify(Resource source, E target) throws EncodeException, IOException {
		return verify(source, IOUtils.DEFAULT_BUFFER_SIZE, target);
	}

	default boolean verify(Resource source, int bufferSize, E target) throws EncodeException, IOException {
		return source.getInputStreamPipeline().optional().filter((is) -> verify(is, bufferSize, target)).isPresent();
	}

	/**
	 * @see #encode(InputStream, int)
	 * @see IOUtils#DEFAULT_BUFFER_SIZE
	 * @param source
	 * @return
	 * @throws IOException
	 * @throws EncodeException
	 */
	default E encode(InputStream source) throws IOException, EncodeException {
		return encode(source, IOUtils.DEFAULT_BUFFER_SIZE);
	}

	@Override
	default <T> FromBytesEncoder<T> toEncoder(Encoder<E, T> encoder) {
		return new NestedFromBytesEncoder<>(this, encoder);
	}

	/**
	 * @see #encode(InputStream, int)
	 */
	@Override
	default E encode(byte[] source) throws EncodeException {
		if (source == null) {
			return null;
		}

		try {
			return encode(new ByteArrayInputStream(source), source.length);
		} catch (IOException e) {
			// 理论上不会执行到这里,除非解码内部抛出io异常
			throw new EncodeException(e);
		}
	}

	default E encode(File source) throws IOException, EncodeException {
		return encode(source, IOUtils.DEFAULT_BUFFER_SIZE);
	}

	default E encode(File source, int bufferSize) throws IOException, EncodeException {
		if (!source.exists()) {
			return null;
		}

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(source);
			return encode(fis, bufferSize);
		} finally {
			IOUtils.close(fis);
		}
	}

	default E encode(Resource source) throws IOException, EncodeException {
		return source.getInputStreamPipeline().optional().map(this::encode).get();
	}

	default E encode(Resource source, int bufferSize) throws IOException, EncodeException {
		return source.getInputStreamPipeline().optional().map((is) -> encode(is, bufferSize)).get();
	}
}
