package run.soeasy.framework.io.pipeline;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

@FunctionalInterface
public interface InputStreamFactoryWrapper<T extends InputStream, W extends InputStreamFactory<T>>
		extends InputStreamFactory<T>, Wrapper<W> {
	@Override
	default Pipeline<T, IOException> getInputStreamPipeline() {
		return getSource().getInputStreamPipeline();
	}

	@Override
	default <R extends Reader> InputFactory<T, R> decode(
			@NonNull ThrowingFunction<? super T, ? extends R, IOException> pipeline) {
		return getSource().decode(pipeline);
	}

	@Override
	default byte[] readAllBytes() throws NoSuchElementException, IOException {
		return getSource().readAllBytes();
	}

	@Override
	default InputFactory<T, Reader> decode() {
		return getSource().decode();
	}

	@Override
	default InputFactory<T, Reader> decode(Charset charset) {
		return getSource().decode(charset);
	}

	@Override
	default InputFactory<T, Reader> decode(CharsetDecoder charsetDecoder) {
		return getSource().decode(charsetDecoder);
	}

	@Override
	default InputFactory<T, Reader> decode(String charsetName) {
		return getSource().decode(charsetName);
	}

	@Override
	default void transferTo(File dest) throws IOException, IllegalStateException {
		getSource().transferTo(dest);
	}

	@Override
	default void transferTo(Path dest) throws IOException, IllegalStateException {
		getSource().transferTo(dest);
	}

	@Override
	default <R extends OutputStream> void transferTo(@NonNull OutputStreamFactory<? extends R> dest)
			throws IOException {
		getSource().transferTo(dest);
	}

	@Override
	default boolean isDecoded() {
		return getSource().isDecoded();
	}
}