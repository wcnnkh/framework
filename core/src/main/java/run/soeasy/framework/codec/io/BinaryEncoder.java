package run.soeasy.framework.codec.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import lombok.NonNull;
import run.soeasy.framework.io.buffer.BufferWriter;

@FunctionalInterface
public interface BinaryEncoder extends BinaryCoder, ByteBufferEncoder<byte[]> {

	@Override
	default byte[] apply(byte[] source) {
		return ByteBufferEncoder.super.apply(source);
	}

	@Override
	default <E extends Throwable> void encode(byte[] source,
			@NonNull BufferWriter<? super ByteBuffer, ? extends E> writer) throws IOException, E {
		try (InputStream inputStream = new ByteArrayInputStream(source)) {
			readInto(inputStream, writer);
		}
	}
}
