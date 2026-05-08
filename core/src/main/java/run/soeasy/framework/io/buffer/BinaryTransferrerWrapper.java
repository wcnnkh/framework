package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import lombok.NonNull;
import run.soeasy.framework.io.source.InputStreamFactory;
import run.soeasy.framework.io.source.OutputStreamFactory;

public interface BinaryTransferrerWrapper<W extends BinaryTransferrer> extends BinaryTransferrer, BufferTransferrerWrapper<ByteBuffer, W> {
	@Override
	default byte[] apply(ByteBuffer input) {
		return getSource().apply(input);
	}
	
	@Override
	default <I extends Throwable> byte[] toBinary(@NonNull BufferReader<? super ByteBuffer, ? extends I> reader)
			throws IOException, I {
		return getSource().toBinary(reader);
	}
	
	@Override
	default <O extends Throwable> byte[] toBinary(@NonNull InputStream input) throws IOException, O {
		return getSource().toBinary(input);
	}
	
	@Override
	default <I extends InputStream, O extends Throwable> byte[] toBinary(@NonNull InputStreamFactory<I> input)
			throws IOException, O {
		return getSource().toBinary(input);
	}
	
	@Override
	default <O extends Throwable> byte[] toBinary(@NonNull ReadableByteChannel input) throws IOException, O {
		return getSource().toBinary(input);
	}
	
	@Override
	default <I extends Throwable> void transfer(@NonNull BufferReader<? super ByteBuffer, ? extends I> input,
			@NonNull OutputStream output) throws IOException, I {
		getSource().transfer(input, output);
	}
	@Override
	default <I extends Throwable, O extends OutputStream> void transfer(
			@NonNull BufferReader<? super ByteBuffer, ? extends I> input, @NonNull OutputStreamFactory<O> output)
			throws IOException, I {
		getSource().transfer(input, output);
	}
	
	@Override
	default <I extends Throwable> void transfer(@NonNull BufferReader<? super ByteBuffer, ? extends I> input,
			@NonNull WritableByteChannel output) throws IOException, I {
		getSource().transfer(input, output);
	}
	@Override
	default <O extends Throwable> void transfer(@NonNull InputStream input,
			@NonNull BufferWriter<? super ByteBuffer, ? extends O> output) throws IOException, O {
		getSource().transfer(input, output);
	}
	
	@Override
	default void transfer(@NonNull InputStream input, @NonNull OutputStream output) throws IOException {
		getSource().transfer(input, output);
	}
	
	@Override
	default <O extends OutputStream> void transfer(@NonNull InputStream input, @NonNull OutputStreamFactory<O> output)
			throws IOException {
		getSource().transfer(input, output);
	}
	
	@Override
	default void transfer(@NonNull InputStream input, @NonNull WritableByteChannel output) throws IOException {
		getSource().transfer(input, output);
	}
	
	@Override
	default <I extends InputStream, O extends Throwable> void transfer(@NonNull InputStreamFactory<I> input,
			@NonNull BufferWriter<? super ByteBuffer, ? extends O> output) throws IOException, O {
		getSource().transfer(input, output);
	}
	
	@Override
	default <I extends InputStream> void transfer(@NonNull InputStreamFactory<I> input, @NonNull OutputStream output)
			throws IOException {
		getSource().transfer(input, output);
	}
	
	@Override
	default <I extends InputStream, O extends OutputStream> void transfer(@NonNull InputStreamFactory<I> input,
			@NonNull OutputStreamFactory<O> output) throws IOException {
		getSource().transfer(input, output);
	}
	
	@Override
	default <I extends InputStream> void transfer(@NonNull InputStreamFactory<I> input,
			@NonNull WritableByteChannel output) throws IOException {
		getSource().transfer(input, output);
	}
	
	@Override
	default <O extends Throwable> void transfer(@NonNull ReadableByteChannel input,
			@NonNull BufferWriter<? super ByteBuffer, ? extends O> output) throws IOException, O {
		getSource().transfer(input, output);
	}
	
	@Override
	default void transfer(@NonNull ReadableByteChannel input, @NonNull OutputStream output) throws IOException {
		getSource().transfer(input, output);
	}
	
	@Override
	default <O extends OutputStream> void transfer(@NonNull ReadableByteChannel input,
			@NonNull OutputStreamFactory<O> output) throws IOException {
		getSource().transfer(input, output);
	}
	
	@Override
	default void transfer(@NonNull ReadableByteChannel input, @NonNull WritableByteChannel output) throws IOException {
		getSource().transfer(input, output);
	}
}
