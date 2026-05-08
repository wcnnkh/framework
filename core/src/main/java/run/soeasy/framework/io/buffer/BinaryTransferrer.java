package run.soeasy.framework.io.buffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.io.ByteBufferInputStream;
import run.soeasy.framework.io.source.InputStreamFactory;
import run.soeasy.framework.io.source.OutputStreamFactory;

public interface BinaryTransferrer extends BufferTransferrer<ByteBuffer>, Function<ByteBuffer, byte[]> {
	@Override
	default byte[] apply(ByteBuffer input) {
		try {
			return toBinary(new ByteBufferInputStream(input));
		} catch (IOException e) {
			throw new IllegalStateException("Internal IO exception", e);
		}
	}

	default <I extends Throwable> byte[] toBinary(@NonNull BufferReader<? super ByteBuffer, ? extends I> reader)
			throws IOException, I {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		transfer(reader, output);
		return output.toByteArray();
	}

	default <O extends Throwable> byte[] toBinary(@NonNull InputStream input) throws IOException, O {
		return toBinary(Channels.newChannel(input));
	}

	default <O extends Throwable> byte[] toBinary(@NonNull ReadableByteChannel input) throws IOException, O {
		return toBinary(input::read);
	}

	default <I extends InputStream, O extends Throwable> byte[] toBinary(@NonNull InputStreamFactory<I> input)
			throws IOException, O {
		try (Pipeline<I, IOException> pipeline = input.getInputStreamPipeline()) {
			return toBinary(pipeline.get());
		}
	}

	default <I extends Throwable> void transfer(@NonNull BufferReader<? super ByteBuffer, ? extends I> reader,
			@NonNull OutputStream output) throws IOException, I {
		transfer(reader, Channels.newChannel(output));
	}

	default <I extends Throwable, O extends OutputStream> void transfer(
			@NonNull BufferReader<? super ByteBuffer, ? extends I> reader, @NonNull OutputStreamFactory<O> output)
			throws IOException, I {
		try (Pipeline<O, IOException> pipeline = output.getOutputStreamPipeline()) {
			transfer(reader, pipeline.get());
		}
	}

	default <I extends Throwable> void transfer(@NonNull BufferReader<? super ByteBuffer, ? extends I> reader,
			@NonNull WritableByteChannel output) throws IOException, I {
		transfer(reader, output::write);
	}

	default <O extends Throwable> void transfer(@NonNull InputStream input,
			@NonNull BufferWriter<? super ByteBuffer, ? extends O> writer) throws IOException, O {
		transfer(Channels.newChannel(input), writer);
	}

	default void transfer(@NonNull InputStream input, @NonNull OutputStream output) throws IOException {
		transfer(Channels.newChannel(input), output);
	}

	default <O extends OutputStream> void transfer(@NonNull InputStream input, @NonNull OutputStreamFactory<O> output)
			throws IOException {
		transfer(Channels.newChannel(input), output);
	}

	default void transfer(@NonNull InputStream input, @NonNull WritableByteChannel output) throws IOException {
		transfer(Channels.newChannel(input), output);
	}

	default <I extends InputStream, O extends Throwable> void transfer(@NonNull InputStreamFactory<I> input,
			@NonNull BufferWriter<? super ByteBuffer, ? extends O> writer) throws IOException, O {
		try (Pipeline<I, IOException> pipeline = input.getInputStreamPipeline()) {
			transfer(pipeline.get(), writer);
		}
	}

	default <I extends InputStream> void transfer(@NonNull InputStreamFactory<I> input, @NonNull OutputStream output)
			throws IOException {
		try (Pipeline<I, IOException> pipeline = input.getInputStreamPipeline()) {
			transfer(pipeline.get(), output);
		}
	}

	default <I extends InputStream, O extends OutputStream> void transfer(@NonNull InputStreamFactory<I> input,
			@NonNull OutputStreamFactory<O> output) throws IOException {
		try (Pipeline<I, IOException> pipeline = input.getInputStreamPipeline()) {
			transfer(pipeline.get(), output);
		}
	}

	default <I extends InputStream> void transfer(@NonNull InputStreamFactory<I> input,
			@NonNull WritableByteChannel output) throws IOException {
		try (Pipeline<I, IOException> pipeline = input.getInputStreamPipeline()) {
			transfer(pipeline.get(), output);
		}
	}

	default <O extends Throwable> void transfer(@NonNull ReadableByteChannel input,
			@NonNull BufferWriter<? super ByteBuffer, ? extends O> writer) throws IOException, O {
		transfer(input::read, writer);
	}

	default void transfer(@NonNull ReadableByteChannel input, @NonNull OutputStream output) throws IOException {
		transfer(input, Channels.newChannel(output));
	}

	default <O extends OutputStream> void transfer(@NonNull ReadableByteChannel input,
			@NonNull OutputStreamFactory<O> output) throws IOException {
		transfer(input::read, output);
	}

	default void transfer(@NonNull ReadableByteChannel input, @NonNull WritableByteChannel output) throws IOException {
		transfer(input::read, output::write);
	}
}
