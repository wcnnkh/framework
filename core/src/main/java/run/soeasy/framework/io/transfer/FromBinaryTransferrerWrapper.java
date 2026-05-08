package run.soeasy.framework.io.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.io.BufferFeeder;
import run.soeasy.framework.io.source.InputStreamFactory;

@FunctionalInterface
public interface FromBinaryTransferrerWrapper<T, W extends FromBinaryTransferrer<T>>
		extends FromBinaryTransferrer<T>, Wrapper<W> {
	@Override
	default <B extends ByteBuffer> T fromBinary(@NonNull BufferFeeder<? super B> source, @NonNull B buffer)
			throws IOException {
		return getSource().fromBinary(source, buffer);
	}
	
	@Override
	default T fromBinary(@NonNull InputStream source, @NonNull ByteBuffer buffer) throws IOException {
		return getSource().fromBinary(source, buffer);
	}

	@Override
	default <I extends InputStream> T fromBinary(@NonNull InputStreamFactory<I> source, @NonNull ByteBuffer buffer)
			throws IOException {
		return getSource().fromBinary(source, buffer);
	}

	@Override
	default T fromBinary(@NonNull ReadableByteChannel source, @NonNull ByteBuffer buffer) throws IOException {
		return getSource().fromBinary(source, buffer);
	}

}
