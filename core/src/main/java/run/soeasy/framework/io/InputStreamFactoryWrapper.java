package run.soeasy.framework.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

public interface InputStreamFactoryWrapper<I extends InputStream, W extends InputStreamFactory<I>>
		extends InputStreamFactory<I>, ReaderFactoryWrapper<Reader, W> {

	@Override
	default InputStreamFactory<I> decode(Charset charset) {
		return getSource().decode(charset);
	}

	@Override
	default InputStreamFactory<I> decode(String charsetName) {
		return getSource().decode(charsetName);
	}

	@Override
	default <T extends Reader> InputStreamFactory<I> decode(
			@NonNull ThrowingFunction<? super I, ? extends T, IOException> decoder) {
		return getSource().decode(decoder);
	}

	@Override
	default InputStream getInputStream() throws IOException {
		return getSource().getInputStream();
	}

	@Override
	default Pipeline<I, IOException> getInputStreamPipeline() {
		return getSource().getInputStreamPipeline();
	}

	@Override
	default Pipeline<Reader, IOException> getReaderPipeline() {
		return getSource().getReaderPipeline();
	}

	@Override
	default boolean isDecoded() {
		return getSource().isDecoded();
	}

	@Override
	default ReadableByteChannel readableChannel() throws IOException {
		return getSource().readableChannel();
	}

	@Override
	default byte[] toByteArray() throws IOException {
		return getSource().toByteArray();
	}

	@Override
	default void transferTo(@NonNull File dest) throws IOException, IllegalStateException {
		getSource().transferTo(dest);
	}

	@Override
	default <R extends OutputStream> long transferTo(@NonNull OutputStreamFactory<? extends R> dest)
			throws IOException {
		return getSource().transferTo(dest);
	}

	@Override
	default void transferTo(@NonNull Path dest) throws IOException {
		getSource().transferTo(dest);
	}
}
