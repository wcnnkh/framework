package run.soeasy.framework.codec.io;

import java.io.IOException;
import java.nio.ByteBuffer;

import lombok.NonNull;
import run.soeasy.framework.io.buffer.BufferReader;

@FunctionalInterface
public interface BinaryDecoder extends BinaryCoder, ByteBufferDecoder<byte[]> {

	@Override
	default byte[] apply(byte[] source) {
		return ByteBufferDecoder.super.apply(source);
	}

	@Override
	default <E extends Throwable> byte[] decode(@NonNull BufferReader<? super ByteBuffer, ? extends E> source)
			throws IOException, E {
		return toByteArray(source);
	}
}
