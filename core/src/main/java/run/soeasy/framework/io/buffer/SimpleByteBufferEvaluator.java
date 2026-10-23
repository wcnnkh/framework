package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.comparator.Comparison;

@RequiredArgsConstructor
@Getter
public class SimpleByteBufferEvaluator<T extends BufferEvaluator<? extends ByteBuffer>> implements ByteBufferEvaluator {
	@NonNull
	private final T comparator;

	@Override
	public <L extends Throwable, R extends Throwable> @NonNull Comparison evaluate(
			@NonNull BufferReader<? super ByteBuffer, ? extends L> left,
			@NonNull BufferReader<? super ByteBuffer, ? extends R> right) throws IOException, L, R {
		return comparator.evaluate(left, right);
	}
}
