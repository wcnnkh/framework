package run.soeasy.framework.io.transfer;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.io.BufferFeeder;
import run.soeasy.framework.io.source.ReaderFactory;

@FunctionalInterface
public interface FromStringTransferrerWrapper<T, W extends FromStringTransferrer<T>>
		extends FromStringTransferrer<T>, Wrapper<W> {
	@Override
	default <B extends CharBuffer> T fromString(@NonNull BufferFeeder<? super B> source, @NonNull B buffer)
			throws IOException {
		return getSource().fromString(source, buffer);
	}

	@Override
	default T fromString(@NonNull Readable source, @NonNull CharBuffer buffer) throws IOException {
		return getSource().fromString(source, buffer);
	}

	@Override
	default <R extends Reader> T fromString(@NonNull ReaderFactory<R> source, @NonNull CharBuffer buffer)
			throws IOException {
		return getSource().fromString(source, buffer);
	}
}
