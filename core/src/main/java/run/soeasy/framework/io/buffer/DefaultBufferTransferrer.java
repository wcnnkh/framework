package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.nio.Buffer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DefaultBufferTransferrer<B extends Buffer> implements BufferTransferrer<B> {
	@NonNull
	private final B buffer;
	@NonNull
	private final BufferTransferrer<B> bufferTransferrer;

	@Override
	public <I extends Throwable, O extends Throwable> void transfer(
			@NonNull BufferReader<? super B, ? extends I> reader, @NonNull BufferWriter<? super B, ? extends O> writer)
			throws IOException, I, O {
		bufferTransferrer.transfer(reader, writer);
	}
}
