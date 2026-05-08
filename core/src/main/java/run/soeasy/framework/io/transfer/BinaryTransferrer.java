package run.soeasy.framework.io.transfer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.BufferFeeder;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.source.InputStreamFactory;
import run.soeasy.framework.io.source.OutputStreamFactory;

@FunctionalInterface
public interface BinaryTransferrer extends ToBinaryTransferrer<ByteBuffer>, FromBinaryTransferrer<ByteBuffer> {
	@Override
	default <B extends ByteBuffer> ByteBuffer fromBinary(@NonNull BufferFeeder<? super B> source, @NonNull B buffer)
			throws IOException {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			binaryTransfer(source, buffer, output);
			return ByteBuffer.wrap(output.toByteArray());
		}
	}

	@Override
	<E extends Throwable> void toBinary(@NonNull ByteBuffer source,
			@NonNull BufferConsumer<? super byte[], ? extends E> target) throws E;

	default <B extends ByteBuffer, E extends Throwable> void binaryTransfer(@NonNull BufferFeeder<? super B> source,
			@NonNull B buffer, @NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E {
		IOUtils.transfer(source, buffer, (b) -> toBinary(b, target));
	}

	default <B extends ByteBuffer, E extends Throwable> void binaryTransfer(@NonNull BufferFeeder<? super B> source,
			@NonNull B buffer, @NonNull OutputStream target) throws IOException, E {
		try (WritableByteChannel channel = Channels.newChannel(target)) {
			binaryTransfer(source, buffer, channel);
		}
	}

	default <B extends ByteBuffer, O extends OutputStream, E extends Throwable> void binaryTransfer(
			@NonNull BufferFeeder<? super B> source, @NonNull B buffer, @NonNull OutputStreamFactory<O> target)
			throws IOException, E {
		try (OutputStream output = target.getOutputStream()) {
			binaryTransfer(source, buffer, output);
		}
	}

	default <B extends ByteBuffer, E extends Throwable> void binaryTransfer(@NonNull BufferFeeder<? super B> source,
			@NonNull B buffer, @NonNull ThrowingConsumer<? super ByteBuffer, ? extends E> target)
			throws IOException, E {
		IOUtils.transfer(source, buffer, (b) -> toBinary(b, target));
	}

	default <B extends ByteBuffer, E extends Throwable> void binaryTransfer(@NonNull BufferFeeder<? super B> source,
			@NonNull B buffer, @NonNull WritableByteChannel target) throws IOException, E {
		IOUtils.transfer(source, buffer, (b) -> toBinary(b, target));
	}

	default <E extends Throwable> void binaryTransfer(@NonNull InputStream source, @NonNull ByteBuffer buffer,
			@NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E {
		try (ReadableByteChannel channel = Channels.newChannel(source)) {
			binaryTransfer(channel, buffer, target);
		}
	}

	default <E extends Throwable> void binaryTransfer(@NonNull InputStream source, @NonNull ByteBuffer buffer,
			@NonNull OutputStream target) throws IOException {
		try (WritableByteChannel channel = Channels.newChannel(target)) {
			binaryTransfer(source, buffer, channel);
		}
	}

	default <O extends OutputStream, E extends Throwable> void binaryTransfer(@NonNull InputStream source,
			@NonNull ByteBuffer buffer, @NonNull OutputStreamFactory<O> target) throws IOException {
		try (OutputStream output = target.getOutputStream()) {
			binaryTransfer(source, buffer, output);
		}
	}

	default <E extends Throwable> void binaryTransfer(@NonNull InputStream source, @NonNull ByteBuffer buffer,
			@NonNull ThrowingConsumer<? super ByteBuffer, ? extends E> target) throws IOException, E {
		binaryTransfer(source, buffer, (a, b, c) -> target.accept(ByteBuffer.wrap(a, b, c)));
	}

	default <E extends Throwable> void binaryTransfer(@NonNull InputStream source, @NonNull ByteBuffer buffer,
			@NonNull WritableByteChannel target) throws IOException {
		binaryTransfer(source, buffer, target::write);
	}

	default <I extends InputStream, E extends Throwable> void binaryTransfer(@NonNull InputStreamFactory<I> source,
			@NonNull ByteBuffer buffer, @NonNull BufferConsumer<? super byte[], ? extends E> target)
			throws IOException, E {
		try (InputStream input = source.getInputStream()) {
			binaryTransfer(input, buffer, target);
		}
	}

	default <I extends InputStream, E extends Throwable> void binaryTransfer(@NonNull InputStreamFactory<I> source,
			@NonNull ByteBuffer buffer, @NonNull OutputStream target) throws IOException, E {
		try (InputStream input = source.getInputStream()) {
			binaryTransfer(input, buffer, target);
		}
	}

	default <I extends InputStream, O extends OutputStream, E extends Throwable> void binaryTransfer(
			@NonNull InputStreamFactory<I> source, @NonNull ByteBuffer buffer, @NonNull OutputStreamFactory<O> target)
			throws IOException, E {
		try (InputStream input = source.getInputStream()) {
			binaryTransfer(input, buffer, target);
		}
	}

	default <I extends InputStream, E extends Throwable> void binaryTransfer(@NonNull InputStreamFactory<I> source,
			@NonNull ByteBuffer buffer, @NonNull ThrowingConsumer<? super ByteBuffer, ? extends E> target)
			throws IOException, E {
		try (InputStream input = source.getInputStream()) {
			binaryTransfer(input, buffer, target);
		}
	}

	default <I extends InputStream, E extends Throwable> void binaryTransfer(@NonNull InputStreamFactory<I> source,
			@NonNull ByteBuffer buffer, @NonNull WritableByteChannel target) throws IOException, E {
		try (InputStream input = source.getInputStream()) {
			binaryTransfer(input, buffer, target);
		}
	}

	default <E extends Throwable> void binaryTransfer(@NonNull ReadableByteChannel source, @NonNull ByteBuffer buffer,
			@NonNull BufferConsumer<? super byte[], ? extends E> target) throws IOException, E {
		binaryTransfer(source::read, buffer, target);
	}

	default <E extends Throwable> void binaryTransfer(@NonNull ReadableByteChannel source, @NonNull ByteBuffer buffer,
			@NonNull OutputStream target) throws IOException, E {
		binaryTransfer(source::read, buffer, target);
	}

	default <O extends OutputStream, E extends Throwable> void binaryTransfer(@NonNull ReadableByteChannel source,
			@NonNull ByteBuffer buffer, @NonNull OutputStreamFactory<O> target) throws IOException, E {
		binaryTransfer(source::read, buffer, target);
	}

	default <E extends Throwable> void binaryTransfer(@NonNull ReadableByteChannel source, @NonNull ByteBuffer buffer,
			@NonNull ThrowingConsumer<? super ByteBuffer, ? extends E> target) throws IOException, E {
		binaryTransfer(source::read, buffer, target);
	}

	default <E extends Throwable> void binaryTransfer(@NonNull ReadableByteChannel source, @NonNull ByteBuffer buffer,
			@NonNull WritableByteChannel target) throws IOException, E {
		binaryTransfer(source::read, buffer, target);
	}
}
