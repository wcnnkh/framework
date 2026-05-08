package run.soeasy.framework.io.transfer;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.io.BufferFeeder;
import run.soeasy.framework.io.source.ReaderFactory;

@FunctionalInterface
public interface FromStringTransferrer<T> {
	default T fromString(@NonNull Readable source, @NonNull CharBuffer buffer) throws IOException {
		return fromString(source::read, buffer);
	}

	default <R extends Reader> T fromString(@NonNull ReaderFactory<R> source, @NonNull CharBuffer buffer)
			throws IOException {
		try (Reader reader = source.getReader()) {
			return fromString(reader, buffer);
		}
	}

	<B extends CharBuffer> T fromString(@NonNull BufferFeeder<? super B> source, @NonNull B buffer) throws IOException;
}
