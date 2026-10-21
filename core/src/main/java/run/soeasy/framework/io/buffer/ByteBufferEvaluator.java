package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.core.comparator.Comparison;

@FunctionalInterface
public interface ByteBufferEvaluator extends BufferEvaluator<ByteBuffer> {
	static <B extends ByteBuffer> ByteBufferEvaluator of(@NonNull BufferEvaluator<B> bufferComparator) {
		if (bufferComparator instanceof ByteBufferEvaluator) {
			return (ByteBufferEvaluator) bufferComparator;
		}
		return new SimpleByteBufferEvaluator<>(bufferComparator);
	}

	default <L extends Throwable> @NonNull Comparison evaluate(@NonNull BufferReader<? super ByteBuffer, ? extends L> left,
			@NonNull ByteBuffer right) throws IOException, L {
		try (InputStream rightInputStream = new ByteBufferInputStream(right)) {
			return evaluate(left, rightInputStream);
		}
	}

	default <L extends Throwable> @NonNull Comparison evaluate(@NonNull BufferReader<? super ByteBuffer, ? extends L> left,
			@NonNull InputStream right) throws IOException, L {
		return evaluate(left, Channels.newChannel(right));
	}

	default <L extends Throwable> @NonNull Comparison evaluate(@NonNull BufferReader<? super ByteBuffer, ? extends L> left,
			@NonNull ReadableByteChannel right) throws IOException, L {
		return evaluate(left, right::read);
	}

	default @NonNull Comparison evaluate(@NonNull ByteBuffer left, @NonNull ByteBuffer right) throws IOException {
		try (ByteBufferInputStream leftInputStream = new ByteBufferInputStream(left)) {
			return evaluate(leftInputStream, right);
		}
	}

	default @NonNull Comparison evaluate(@NonNull ByteBuffer left, @NonNull InputStream right) throws IOException {
		return evaluate(left, Channels.newChannel(right));
	}

	default @NonNull Comparison evaluate(@NonNull ByteBuffer left, @NonNull ReadableByteChannel right) throws IOException {
		try (ByteBufferInputStream leftInputStream = new ByteBufferInputStream(left)) {
			return evaluate(leftInputStream, right);
		}
	}

	default @NonNull <R extends Throwable> Comparison evaluate(@NonNull ByteBuffer left,
			@NonNull BufferReader<? super ByteBuffer, ? extends R> right) throws IOException, R {
		try (ByteBufferInputStream leftInputStream = new ByteBufferInputStream(left)) {
			return evaluate(leftInputStream, right);
		}
	}

	default @NonNull Comparison evaluate(@NonNull InputStream left, @NonNull InputStream right) throws IOException {
		return evaluate(Channels.newChannel(left), right);
	}

	default @NonNull Comparison evaluate(@NonNull InputStream left, @NonNull ByteBuffer right) throws IOException {
		try (InputStream rightInputStream = new ByteBufferInputStream(right)) {
			return evaluate(left, rightInputStream);
		}
	}

	default @NonNull Comparison evaluate(@NonNull InputStream left, @NonNull ReadableByteChannel right)
			throws IOException {
		return evaluate(Channels.newChannel(left), right);
	}

	default @NonNull <R extends Throwable> Comparison evaluate(@NonNull InputStream left,
			@NonNull BufferReader<? super ByteBuffer, ? extends R> right) throws IOException, R {
		return evaluate(Channels.newChannel(left), right);
	}

	default @NonNull Comparison evaluate(@NonNull ReadableByteChannel left, @NonNull ReadableByteChannel right)
			throws IOException {
		return evaluate(left, right::read);
	}

	default @NonNull Comparison evaluate(@NonNull ReadableByteChannel left, @NonNull ByteBuffer right) throws IOException {
		try (ByteBufferInputStream rightInputStream = new ByteBufferInputStream(right)) {
			return evaluate(left, rightInputStream);
		}
	}

	default @NonNull Comparison evaluate(@NonNull ReadableByteChannel left, @NonNull InputStream right)
			throws IOException {
		return evaluate(left, Channels.newChannel(right));
	}

	default @NonNull <R extends Throwable> Comparison evaluate(@NonNull ReadableByteChannel left,
			@NonNull BufferReader<? super ByteBuffer, ? extends R> right) throws IOException, R {
		return evaluate(left::read, right);
	}
}
