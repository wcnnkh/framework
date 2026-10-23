package run.soeasy.framework.codec.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.codec.Coder;
import run.soeasy.framework.core.comparator.Comparison;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.buffer.BufferReader;

@FunctionalInterface
public interface ByteBufferEncoder<T> extends Encoder<T, ByteBuffer>, Coder<T, byte[]> {
	@Override
	default byte[] apply(T source) {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			encode(source, output);
			return output.toByteArray();
		} catch (IOException e) {
			throw new IllegalStateException("Internal error", e);
		}
	}

	default void encode(T source, @NonNull OutputStream writer) throws IOException {
		encode(source, Channels.newChannel(writer));
	}

	default void encode(T source, @NonNull WritableByteChannel writer) throws IOException {
		encode(source, writer::write);
	}

	default <E extends Throwable> @NonNull Comparison evaluate(T left,
			@NonNull BufferReader<? super ByteBuffer, ? extends E> right) throws IOException, E {
		return evaluate(left, IOUtils.binary().toByteArray(right));
	}

	default <E extends Throwable> @NonNull Comparison evaluate(T left, @NonNull ByteBuffer right)
			throws IOException, E {
		return evaluate(left, IOUtils.binary().toByteArray(right));
	}

	default @NonNull Comparison evaluate(T left, @NonNull InputStream right) throws IOException {
		return evaluate(left, IOUtils.binary().toByteArray(right));
	}

	default @NonNull Comparison evaluate(T left, @NonNull ReadableByteChannel right) throws IOException {
		return evaluate(left, IOUtils.binary().toByteArray(right));
	}
}
