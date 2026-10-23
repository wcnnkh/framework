package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.core.comparator.Comparison;

public interface StreamCharBufferEvaluator extends CharBufferEvaluator {

	@Override
	default <L extends Throwable, R extends Throwable> @NonNull Comparison evaluate(
			@NonNull BufferReader<? super CharBuffer, ? extends L> left,
			@NonNull BufferReader<? super CharBuffer, ? extends R> right) throws IOException, L, R {
		try (Reader r = new CharBufferReader(left)) {
			return evaluate(r, right);
		}
	}

	@Override
	@NonNull
	Comparison evaluate(@NonNull Reader left, @NonNull Reader right) throws IOException;
}
