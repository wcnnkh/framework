package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.core.comparator.Comparison;

@FunctionalInterface
public interface CharBufferEvaluator extends BufferEvaluator<CharBuffer> {
	static <B extends CharBuffer> CharBufferEvaluator of(@NonNull BufferEvaluator<B> bufferComparator) {
		if (bufferComparator instanceof CharBufferEvaluator) {
			return (CharBufferEvaluator) bufferComparator;
		}
		return new SimpleCharBufferEvaluator<>(bufferComparator);
	}

	default <L extends Exception> @NonNull Comparison evaluate(
			@NonNull BufferReader<? super CharBuffer, ? extends L> left, @NonNull CharBuffer right)
			throws IOException, L {
		return evaluate(left, right::read);
	}

	default <L extends Exception> @NonNull Comparison evaluate(
			@NonNull BufferReader<? super CharBuffer, ? extends L> left, @NonNull Reader right) throws IOException, L {
		return evaluate(left, right::read);
	}

	default @NonNull Comparison evaluate(@NonNull CharBuffer left, @NonNull CharBuffer right) throws IOException {
		return evaluate(left::read, right::read);
	}

	default <R extends Throwable> @NonNull Comparison evaluate(@NonNull CharBuffer left,
			@NonNull BufferReader<? super CharBuffer, ? extends R> right) throws IOException, R {
		return evaluate(left::read, right);
	}

	default @NonNull Comparison evaluate(@NonNull CharBuffer left, @NonNull Reader right) throws IOException {
		return evaluate(left::read, right::read);
	}

	default @NonNull Comparison evaluate(@NonNull Reader left, @NonNull Reader right) throws IOException {
		return evaluate(left::read, right::read);
	}

	default <R extends Throwable> @NonNull Comparison evaluate(@NonNull Reader left,
			@NonNull BufferReader<? super CharBuffer, ? extends R> right) throws IOException, R {
		return evaluate(left::read, right);
	}

	default @NonNull Comparison evaluate(@NonNull Reader left, @NonNull CharBuffer right) throws IOException {
		return evaluate(left::read, right::read);
	}
}