package run.soeasy.framework.io.transfer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.source.OutputStreamFactory;

@FunctionalInterface
public interface ToBinaryTransferrerWrapper<S, W extends ToBinaryTransferrer<S>>
		extends ToBinaryTransferrer<S>, Wrapper<W> {

	@Override
	default <E extends Throwable> void toBinary(S source,
			@NonNull ThrowingConsumer<? super ByteBuffer, ? extends E> target) throws E {
		getSource().toBinary(source, target);
	}

	@Override
	default <E extends Throwable> void toBinary(S source, @NonNull BufferConsumer<? super byte[], ? extends E> target)
			throws E {
		getSource().toBinary(source, target);
	}

	@Override
	default <E extends Throwable> void toBinary(S source, @NonNull OutputStream target) throws IOException {
		getSource().toBinary(source, target);
	}

	@Override
	default <O extends OutputStream, E extends Throwable> void toBinary(S source,
			@NonNull OutputStreamFactory<O> target) throws IOException {
		getSource().toBinary(source, target);
	}

	@Override
	default <E extends Throwable> void toBinary(S source, @NonNull WritableByteChannel target) throws IOException {
		getSource().toBinary(source, target);
	}

	@Override
	default byte[] toBinary(S source) {
		return getSource().toBinary(source);
	}
}
