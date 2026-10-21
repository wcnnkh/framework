package run.soeasy.framework.codec.io;

import java.io.IOException;
import java.nio.CharBuffer;

import lombok.NonNull;
import run.soeasy.framework.codec.UnaryCoder;
import run.soeasy.framework.core.comparator.Comparison;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.buffer.BufferReader;
import run.soeasy.framework.io.buffer.CharBufferEvaluator;
import run.soeasy.framework.io.buffer.CharBufferTransporter;

@FunctionalInterface
public interface StringCoder extends UnaryCoder<String>, CharBufferTransporter, CharBufferEvaluator {

	@Override
	default String apply(String source) {
		return readToString(CharBuffer.wrap(source));
	}

	@Override
	default <L extends Throwable, R extends Throwable> @NonNull Comparison evaluate(
			@NonNull BufferReader<? super CharBuffer, ? extends L> left,
			@NonNull BufferReader<? super CharBuffer, ? extends R> right) throws IOException, L, R {
		String leftString = IOUtils.text().readToString(left);
		String rightString = IOUtils.text().readToString(right);
		return evaluate(leftString, rightString);
	}
}
