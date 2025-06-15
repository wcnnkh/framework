package run.soeasy.framework.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

@FunctionalInterface
public interface InputStreamFactory<I extends InputStream> extends ReaderFactory<Reader> {
	default InputStreamFactory<I> decode(@NonNull Charset charset) {
		return new DecodedInputStreamFactory<>(this, charset, (e) -> new InputStreamReader(e, charset));
	}

	default InputStreamFactory<I> decode(@NonNull String charsetName) {
		return new DecodedInputStreamFactory<>(this, charsetName, (e) -> new InputStreamReader(e, charsetName));
	}

	default <T extends Reader> InputStreamFactory<I> decode(
			@NonNull ThrowingFunction<? super I, ? extends T, IOException> decoder) {
		return new DecodedInputStreamFactory<>(this, null, decoder);
	}

	default InputStream getInputStream() throws IOException {
		return new InputStreamPipeline(getInputStreamPipeline());
	}

	@NonNull
	Pipeline<I, IOException> getInputStreamPipeline();

	@Override
	default @NonNull Pipeline<Reader, IOException> getReaderPipeline() {
		return getInputStreamPipeline().map((e) -> (Reader) new InputStreamReader(e)).onClose((e) -> e.close());
	}

	default boolean isDecoded() {
		return false;
	}

	default ReadableByteChannel readableChannel() throws IOException {
		return Channels.newChannel(getInputStream());
	}

	default byte[] toByteArray() throws IOException {
		InputStream input = getInputStream();
		try {
			return IOUtils.toByteArray(input);
		} finally {
			input.close();
		}
	}

	default void transferTo(@NonNull File dest) throws IOException, IllegalStateException {
		InputStream input = getInputStream();
		try {
			FileUtils.transferTo(input, dest);
		} finally {
			input.close();
		}
	}

	default <R extends OutputStream> long transferTo(@NonNull OutputStreamFactory<? extends R> dest)
			throws IOException {
		InputStream input = getInputStream();
		try {
			OutputStream out = dest.getOutputStream();
			try {
				return IOUtils.transferTo(input, out);
			} finally {
				out.close();
			}
		} finally {
			input.close();
		}
	}

	default void transferTo(@NonNull Path dest) throws IOException {
		InputStream input = getInputStream();
		try {
			FileUtils.transferTo(input, dest);
		} finally {
			input.close();
		}
	}
}
