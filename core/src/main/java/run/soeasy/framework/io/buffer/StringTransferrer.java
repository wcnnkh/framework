package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.io.CharSequenceReader;
import run.soeasy.framework.io.source.ReaderFactory;
import run.soeasy.framework.io.source.WriterFactory;

public interface StringTransferrer extends BufferTransferrer<CharBuffer>, Function<CharSequence, String> {
	
	@Override
	default String apply(CharSequence input) {
		try {
			return toString(new CharSequenceReader(input));
		} catch (IOException e) {
			throw new IllegalStateException("Internal IO exception", e);
		}
	}
	
	default <I extends IOException> String toString(@NonNull BufferReader<? super CharBuffer, ? extends I> reader)
			throws IOException, I {
		StringBuilder output = new StringBuilder();
		transfer(reader, output);
		return output.toString();
	}

	default String toString(@NonNull Readable readable) throws IOException {
		return toString(readable::read);
	}

	default <I extends Reader> String toString(@NonNull ReaderFactory<I> input) throws IOException {
		try (Pipeline<I, IOException> pipeline = input.getReaderPipeline()) {
			return toString(pipeline.get());
		}
	}

	default <I extends Throwable> void transfer(@NonNull BufferReader<? super CharBuffer, ? extends I> input,
			@NonNull Appendable output) throws IOException, I {
		transfer(input, output::append);
	}

	default <I extends Throwable, O extends Writer> void transfer(
			@NonNull BufferReader<? super CharBuffer, ? extends I> input, @NonNull WriterFactory<O> output)
			throws IOException, I {
		try (Pipeline<O, IOException> pipeline = output.getWriterPipeline()) {
			transfer(input, pipeline.get());
		}
	}

	default void transfer(@NonNull Readable input, @NonNull Appendable output) throws IOException {
		transfer(input, output::append);
	}

	default <O extends Throwable> void transfer(@NonNull Readable input,
			@NonNull BufferWriter<? super CharBuffer, ? extends O> output) throws IOException, O {
		transfer(input::read, output);
	}

	default <O extends Writer> void transfer(@NonNull Readable input, @NonNull WriterFactory<O> output)
			throws IOException {
		try (Pipeline<O, IOException> pipeline = output.getWriterPipeline()) {
			transfer(input, pipeline.get());
		}
	}

	default <I extends Reader, O extends Throwable> void transfer(@NonNull ReaderFactory<I> input,
			@NonNull BufferWriter<? super CharBuffer, ? extends O> output) throws IOException, O {
		try (Pipeline<I, IOException> pipeline = input.getReaderPipeline()) {
			transfer(pipeline.get(), output);
		}
	}

	default <I extends Reader> void transfer(@NonNull ReaderFactory<I> input, @NonNull Writer output)
			throws IOException {
		try (Pipeline<I, IOException> pipeline = input.getReaderPipeline()) {
			transfer(pipeline.get(), output);
		}
	}

	default <I extends Reader, O extends Writer> void transfer(@NonNull ReaderFactory<I> input,
			@NonNull WriterFactory<O> output) throws IOException {
		try (Pipeline<I, IOException> pipeline = input.getReaderPipeline()) {
			transfer(pipeline.get(), output);
		}
	}
}
