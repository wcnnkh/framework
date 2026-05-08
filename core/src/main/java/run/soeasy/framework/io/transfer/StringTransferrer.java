package run.soeasy.framework.io.transfer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.BufferFeeder;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.source.ReaderFactory;
import run.soeasy.framework.io.source.WriterFactory;

@FunctionalInterface
public interface StringTransferrer extends ToStringTransferrer<CharBuffer>, FromStringTransferrer<CharBuffer> {

	@Override
	default <B extends CharBuffer> CharBuffer fromString(@NonNull BufferFeeder<? super B> source, @NonNull B buffer)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		stringTransfer(source, buffer, sb);
		return CharBuffer.wrap(sb);
	}

	default <B extends CharBuffer, E extends Throwable> void stringTransfer(@NonNull BufferFeeder<? super B> source,
			@NonNull B buffer, @NonNull Appendable target) throws IOException, E {
		IOUtils.transfer(source, buffer, (b) -> toString(b, target));
	}

	default <B extends CharBuffer, E extends Throwable> void stringTransfer(@NonNull BufferFeeder<? super B> source,
			@NonNull B buffer, @NonNull BufferConsumer<? super char[], ? extends E> target) throws IOException, E {
		IOUtils.transfer(source, buffer, (b) -> toString(b, target));
	}

	default <B extends CharBuffer, E extends Throwable> void stringTransfer(@NonNull BufferFeeder<? super B> source,
			@NonNull B buffer, @NonNull ThrowingConsumer<? super CharBuffer, ? extends E> target)
			throws IOException, E {
		IOUtils.transfer(source, buffer, (b) -> toString(b, target));
	}

	default <B extends CharBuffer, W extends Writer, E extends Throwable> void stringTransfer(
			@NonNull BufferFeeder<? super B> source, @NonNull B buffer, @NonNull WriterFactory<W> target)
			throws IOException, E {
		try (Writer writer = target.getWriter()) {
			stringTransfer(source, buffer, writer);
		}
	}

	default <E extends Throwable> void stringTransfer(@NonNull Readable source, @NonNull CharBuffer buffer,
			@NonNull Appendable target) throws IOException, E {
		stringTransfer(source::read, buffer, target);
	}

	default <E extends Throwable> void stringTransfer(@NonNull Readable source, @NonNull CharBuffer buffer,
			@NonNull BufferConsumer<? super char[], ? extends E> target) throws IOException, E {
		stringTransfer(source::read, buffer, target);
	}

	default <E extends Throwable> void stringTransfer(@NonNull Readable source, @NonNull CharBuffer buffer,
			@NonNull ThrowingConsumer<? super CharBuffer, ? extends E> target) throws IOException, E {
		stringTransfer(source::read, buffer, target);
	}

	default <W extends Writer, E extends Throwable> void stringTransfer(@NonNull Readable source,
			@NonNull CharBuffer buffer, @NonNull WriterFactory<W> target) throws IOException, E {
		stringTransfer(source::read, buffer, target);
	}

	default <R extends Reader, E extends Throwable> void stringTransfer(@NonNull ReaderFactory<R> source,
			@NonNull CharBuffer buffer, @NonNull Appendable target) throws IOException, E {
		try (Reader reader = source.getReader()) {
			stringTransfer(reader, buffer, target);
		}
	}

	default <R extends Reader, E extends Throwable> void stringTransfer(@NonNull ReaderFactory<R> source,
			@NonNull CharBuffer buffer, @NonNull BufferConsumer<? super char[], ? extends E> target)
			throws IOException, E {
		try (Reader reader = source.getReader()) {
			stringTransfer(reader, buffer, target);
		}
	}

	default <R extends Reader, E extends Throwable> void stringTransfer(@NonNull ReaderFactory<R> source,
			@NonNull CharBuffer buffer, @NonNull ThrowingConsumer<? super CharBuffer, ? extends E> target)
			throws IOException, E {
		try (Reader reader = source.getReader()) {
			stringTransfer(reader, buffer, target);
		}
	}

	default <R extends Reader, W extends Writer, E extends Throwable> void stringTransfer(
			@NonNull ReaderFactory<R> source, @NonNull CharBuffer buffer, @NonNull WriterFactory<W> target)
			throws IOException, E {
		try (Reader reader = source.getReader()) {
			stringTransfer(reader, buffer, target);
		}
	}

	@Override
	<E extends Throwable> void toString(CharBuffer source, BufferConsumer<? super char[], ? extends E> target) throws E;
}
