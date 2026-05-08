package run.soeasy.framework.io.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.io.BufferFeeder;
import run.soeasy.framework.io.source.InputStreamFactory;

@FunctionalInterface
public interface FromBinaryTransferrer<T> {
	<B extends ByteBuffer> T fromBinary(@NonNull BufferFeeder<? super B> source, @NonNull B buffer) throws IOException;

	default T fromBinary(@NonNull InputStream source, @NonNull ByteBuffer buffer) throws IOException {
		try (ReadableByteChannel channel = Channels.newChannel(source)) {
			return fromBinary(channel, buffer);
		}
	}

	default T fromBinary(@NonNull ReadableByteChannel source, @NonNull ByteBuffer buffer) throws IOException {
		return fromBinary(source::read, buffer);
	}

	default <I extends InputStream> T fromBinary(@NonNull InputStreamFactory<I> source, @NonNull ByteBuffer buffer)
			throws IOException {
		try (InputStream input = source.getInputStream()) {
			return fromBinary(input, buffer);
		}
	}
}
