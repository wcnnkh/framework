package run.soeasy.framework.codec.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.codec.Coder;
import run.soeasy.framework.core.comparator.Comparison;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.buffer.BufferReader;
import run.soeasy.framework.io.buffer.ByteBufferInputStream;

@FunctionalInterface
public interface ByteBufferDecoder<T> extends Decoder<T, ByteBuffer>, Coder<byte[], T> {
	@Override
	default T apply(byte[] source) {
		try {
			return decode(ByteBuffer.wrap(source));
		} catch (IOException e) {
			throw new IllegalStateException("Internal error", e);
		}
	}

	default T decode(@NonNull ByteBuffer source) throws IOException {
		try (InputStream input = new ByteBufferInputStream(source)) {
			return decode(input);
		}
	}

	default T decode(@NonNull InputStream source) throws IOException {
		return decode(Channels.newChannel(source));
	}

	default T decode(@NonNull ReadableByteChannel source) throws IOException {
		return decode(source::read);
	}
	
	default <E extends Throwable> @NonNull Comparison evaluate(
			@NonNull BufferReader<? super ByteBuffer, ? extends E> left, T right) throws IOException, E {
		return evaluate(IOUtils.binary().toByteArray(left), right);
	}

	default @NonNull Comparison evaluate(@NonNull ByteBuffer left, T right) throws IOException {
		return evaluate(IOUtils.binary().toByteArray(left), right);
	}

	default @NonNull Comparison evaluate(@NonNull InputStream left, T right) throws IOException {
		return evaluate(IOUtils.binary().toByteArray(left), right);
	}

	default @NonNull Comparison evaluate(@NonNull ReadableByteChannel left, T right) throws IOException {
		return evaluate(IOUtils.binary().toByteArray(left), right);
	}
}
