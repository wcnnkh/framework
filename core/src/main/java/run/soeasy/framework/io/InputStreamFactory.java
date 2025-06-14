package run.soeasy.framework.io;

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
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.io.file.FileUtils;

@FunctionalInterface
public interface InputStreamFactory<T extends InputStream> {

	@NonNull
	Pipeline<T, IOException> getInputStreamPipeline();

	default boolean isDecoded() {
		return false;
	}

	default <R extends Reader> InputFactory<T, R> decode(
			@NonNull ThrowingFunction<? super T, ? extends R, IOException> pipeline) {
		return new StandardDecodedInputStreamFactory<>(this, pipeline);
	}

	default byte[] readAllBytes() throws NoSuchElementException, IOException {
		return getInputStreamPipeline().map(IOUtils::toByteArray).get();
	}

	default InputFactory<T, Reader> decode() {
		return new DefaultDecodedInputStreamFactory<>(this);
	}

	default InputFactory<T, Reader> decode(@NonNull Charset charset) {
		return new StandardCharsetInputStreamFactory<>(this, charset);
	}

	default InputFactory<T, Reader> decode(@NonNull CharsetDecoder charsetDecoder) {
		return new DefaultDecodedInputStreamFactory<>(this, charsetDecoder);
	}

	default InputFactory<T, Reader> decode(@NonNull String charsetName) {
		return new StandardCharsetInputStreamFactory<>(this, charsetName);
	}

	default void transferTo(@NonNull File dest) throws IOException, IllegalStateException {
		getInputStreamPipeline().optional().ifPresent((is) -> FileUtils.copyInputStreamToFile(is, dest));
	}

	default void transferTo(@NonNull Path dest) throws IOException, IllegalStateException {
		getInputStreamPipeline().optional().ifPresent((is) -> FileUtils.copyInputStreamToPath(is, dest));
	}

	default <R extends OutputStream> void transferTo(@NonNull OutputStreamFactory<? extends R> dest)
			throws IOException {
		getInputStreamPipeline().optional()
				.ifPresent((is) -> dest.getOutputStreamPipeline().optional().ifPresent((os) -> IOUtils.copy(is, os)));
	}
}