package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.nio.Buffer;

import lombok.NonNull;

@FunctionalInterface
public interface BufferTransferrer<B extends Buffer> {
	<I extends Throwable, O extends Throwable> void transfer(@NonNull BufferReader<? super B, ? extends I> reader,
			@NonNull BufferWriter<? super B, ? extends O> writer) throws IOException, I, O;
}
