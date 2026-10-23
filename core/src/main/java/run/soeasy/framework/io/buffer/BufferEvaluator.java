package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.nio.Buffer;

import lombok.NonNull;
import run.soeasy.framework.core.comparator.Comparison;

@FunctionalInterface
public interface BufferEvaluator<B extends Buffer> {
	@NonNull
	<L extends Throwable, R extends Throwable> Comparison evaluate(@NonNull BufferReader<? super B, ? extends L> left,
			@NonNull BufferReader<? super B, ? extends R> right) throws IOException, L, R;
}
