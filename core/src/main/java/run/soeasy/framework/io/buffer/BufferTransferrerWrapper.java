package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.nio.Buffer;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;

@FunctionalInterface
public interface BufferTransferrerWrapper<B extends Buffer, W extends BufferTransferrer<B>>
		extends BufferTransferrer<B>, Wrapper<W> {
	@Override
	default <I extends Throwable, O extends Throwable> void transfer(@NonNull BufferReader<? super B, ? extends I> input,
			@NonNull BufferWriter<? super B, ? extends O> output) throws IOException, I, O {
		getSource().transfer(input, output);
	}
}
