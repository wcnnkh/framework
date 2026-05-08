package run.soeasy.framework.io.transfer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.source.OutputStreamFactory;

@FunctionalInterface
public interface ToBinaryTransferrer<S> {
	default byte[] toBinary(S source) {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try {
			toBinary(source, target);
		} catch (IOException e) {
			throw new IllegalStateException("Internal IO exception", e);
		} finally {
			IOUtils.closeQuietly(target);
		}
		return target.toByteArray();
	}

	<E extends Throwable> void toBinary(S source, @NonNull BufferConsumer<? super byte[], ? extends E> target) throws E;

	default <E extends Throwable> void toBinary(S source, @NonNull OutputStream target) throws IOException {
		try (WritableByteChannel channel = Channels.newChannel(target)) {
			toBinary(source, channel);
		}
	}

	default <O extends OutputStream, E extends Throwable> void toBinary(S source,
			@NonNull OutputStreamFactory<O> target) throws IOException {
		try (OutputStream output = target.getOutputStream()) {
			toBinary(source, output);
		}
	}

	default <E extends Throwable> void toBinary(S source,
			@NonNull ThrowingConsumer<? super ByteBuffer, ? extends E> target) throws E {
		toBinary(source, (a, b, c) -> target.accept(ByteBuffer.wrap(a, b, c)));
	}

	default <E extends Throwable> void toBinary(S source, @NonNull WritableByteChannel target) throws IOException {
		toBinary(source, target::write);
	}
}
