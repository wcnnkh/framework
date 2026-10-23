package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.nio.CharBuffer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.comparator.Comparison;

@RequiredArgsConstructor
@Getter
public class SimpleCharBufferEvaluator<T extends BufferEvaluator<? extends CharBuffer>> implements CharBufferEvaluator {
	@NonNull
	private final T comparator;

	@Override
	public <L extends Throwable, R extends Throwable> @NonNull Comparison evaluate(
			@NonNull BufferReader<? super CharBuffer, ? extends L> left,
			@NonNull BufferReader<? super CharBuffer, ? extends R> right) throws IOException, L, R {
		return comparator.evaluate(left, right);
	}
}
