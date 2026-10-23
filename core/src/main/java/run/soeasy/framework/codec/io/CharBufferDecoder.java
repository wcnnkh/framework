package run.soeasy.framework.codec.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.codec.Coder;
import run.soeasy.framework.core.comparator.Comparison;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.buffer.BufferReader;

public interface CharBufferDecoder<T> extends Decoder<T, CharBuffer>, Coder<String, T> {
	@Override
	default T apply(String source) {
		try {
			return decode(CharBuffer.wrap(source));
		} catch (IOException e) {
			throw new IllegalStateException("Internal error", e);
		}
	}

	default T decode(@NonNull CharBuffer source) throws IOException {
		return decode(source::read);
	}

	default T decode(@NonNull Reader source) throws IOException {
		return decode(source::read);
	}

	default <E extends Throwable> @NonNull Comparison evaluate(
			@NonNull BufferReader<? super CharBuffer, ? extends E> left, T right) throws IOException, E {
		return evaluate(IOUtils.text().toCharSequence(left).toString(), right);
	}

	default @NonNull Comparison evaluate(CharBuffer left, T right) throws IOException {
		return evaluate(IOUtils.text().toCharSequence(left).toString(), right);
	}

	default @NonNull Comparison evaluate(Reader left, T right) throws IOException {
		return evaluate(IOUtils.text().toCharSequence(left).toString(), right);
	}
}
