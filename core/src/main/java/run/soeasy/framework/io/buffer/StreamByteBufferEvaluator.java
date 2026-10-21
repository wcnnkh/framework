package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import lombok.NonNull;
import run.soeasy.framework.core.comparator.Comparison;

public interface StreamByteBufferEvaluator extends ByteBufferEvaluator {
	@Override
	default <L extends Throwable, R extends Throwable> @NonNull Comparison evaluate(
			@NonNull BufferReader<? super ByteBuffer, ? extends L> left,
			@NonNull BufferReader<? super ByteBuffer, ? extends R> right) throws IOException, L, R {
		try (BufferReaderChannel leftChannel = new BufferReaderChannel(left);) {
			return evaluate(leftChannel, right);
		}
	}

	@Override
	@NonNull
	Comparison evaluate(@NonNull InputStream left, @NonNull InputStream right) throws IOException;
}
