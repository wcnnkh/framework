package run.soeasy.framework.codec.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.codec.Coder;
import run.soeasy.framework.core.comparator.Comparison;
import run.soeasy.framework.io.IOUtils;

@FunctionalInterface
public interface CharBufferEncoder<T> extends Encoder<T, CharBuffer>, Coder<T, String> {
	@Override
	default String apply(T t) {
		StringBuilder sb = new StringBuilder();
		try {
			encode(t, sb::append);
		} catch (IOException e) {
			throw new IllegalStateException("Internal error", e);
		}
		return sb.toString();
	}

	default <E extends Throwable> void encode(T source, @NonNull Writer writer) throws IOException, E {
		encode(source, writer::append);
	}

	default @NonNull Comparison evaluate(T left, CharBuffer right) throws IOException {
		return evaluate(left, IOUtils.text().toCharSequence(right).toString());
	}

	default @NonNull Comparison evaluate(T left, Reader right) throws IOException {
		return evaluate(left, IOUtils.text().toCharSequence(right).toString());
	}
}
