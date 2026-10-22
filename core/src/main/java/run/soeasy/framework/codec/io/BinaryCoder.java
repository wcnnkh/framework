package run.soeasy.framework.codec.io;

import java.io.IOException;
import java.nio.ByteBuffer;

import lombok.NonNull;
import run.soeasy.framework.codec.UnaryCoder;
import run.soeasy.framework.core.comparator.Comparison;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.buffer.BufferReader;
import run.soeasy.framework.io.buffer.ByteBufferEvaluator;
import run.soeasy.framework.io.buffer.ByteBufferTransporter;

@FunctionalInterface
public interface BinaryCoder extends UnaryCoder<byte[]>, ByteBufferTransporter, ByteBufferEvaluator {
	@Override
	default byte[] apply(byte[] source) {
		try {
			return readToByteArray(ByteBuffer.wrap(source));
		} catch (IOException e) {
			throw new IllegalStateException("Internal error", e);
		}
	}

	@Override
	default <L extends Throwable, R extends Throwable> @NonNull Comparison evaluate(
			@NonNull BufferReader<? super ByteBuffer, ? extends L> left,
			@NonNull BufferReader<? super ByteBuffer, ? extends R> right) throws IOException, L, R {
		byte[] leftByteArray = IOUtils.binary().readToByteArray(left);
		byte[] rightByteArray = IOUtils.binary().readToByteArray(right);
		return evaluate(leftByteArray, rightByteArray);
	}
}
