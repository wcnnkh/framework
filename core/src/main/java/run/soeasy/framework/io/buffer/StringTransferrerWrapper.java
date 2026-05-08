package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.io.source.ReaderFactory;
import run.soeasy.framework.io.source.WriterFactory;

@FunctionalInterface
public interface StringTransferrerWrapper<W extends StringTransferrer>
		extends StringTransferrer, BufferTransferrerWrapper<CharBuffer, W> {
	@Override
	default <I extends Throwable> void transfer(@NonNull BufferReader<? super CharBuffer, ? extends I> input,
			@NonNull Appendable output) throws IOException, I {
		getSource().transfer(input, output);
	}

	@Override
	default <I extends Throwable, O extends Writer> void transfer(
			@NonNull BufferReader<? super CharBuffer, ? extends I> input, @NonNull WriterFactory<O> output)
			throws IOException, I {
		getSource().transfer(input, output);
	}

	@Override
	default void transfer(@NonNull Readable input, @NonNull Appendable output) throws IOException {
		getSource().transfer(input, output);
	}

	@Override
	default <O extends Throwable> void transfer(@NonNull Readable input,
			@NonNull BufferWriter<? super CharBuffer, ? extends O> output) throws IOException, O {
		getSource().transfer(input, output);
	}

	@Override
	default <O extends Writer> void transfer(@NonNull Readable input, @NonNull WriterFactory<O> output)
			throws IOException {
		getSource().transfer(input, output);
	}

	@Override
	default <I extends Reader, O extends Throwable> void transfer(@NonNull ReaderFactory<I> input,
			@NonNull BufferWriter<? super CharBuffer, ? extends O> output) throws IOException, O {
		getSource().transfer(input, output);
	}

	@Override
	default <I extends Reader> void transfer(@NonNull ReaderFactory<I> input, @NonNull Writer output)
			throws IOException {
		getSource().transfer(input, output);
	}

	@Override
	default <I extends Reader, O extends Writer> void transfer(@NonNull ReaderFactory<I> input,
			@NonNull WriterFactory<O> output) throws IOException {
		getSource().transfer(input, output);
	}
}
