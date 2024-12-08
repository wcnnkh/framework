package io.basc.framework.util.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import io.basc.framework.util.Channel;
import io.basc.framework.util.Pipeline;
import io.basc.framework.util.Wrapper;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@FunctionalInterface
public interface InputStreamFactory<T extends InputStream> {
	@FunctionalInterface
	public static interface InputStreamFactoryWrapper<T extends InputStream, W extends InputStreamFactory<T>>
			extends InputStreamFactory<T>, Wrapper<W> {
		@Override
		default Channel<T, IOException> getInputStream() {
			return getSource().getInputStream();
		}

		@Override
		default <R extends Reader> ReaderFactory<R> map(
				@NonNull Pipeline<? super T, ? extends R, ? extends IOException> pipeline) {
			return getSource().map(pipeline);
		}

		@Override
		default byte[] readAllBytes() throws NoSuchElementException, IOException {
			return getSource().readAllBytes();
		}

		@Override
		default ReaderFactory<Reader> toReaderFactory() {
			return getSource().toReaderFactory();
		}

		@Override
		default ReaderFactory<Reader> toReaderFactory(Charset charset) {
			return getSource().toReaderFactory(charset);
		}

		@Override
		default ReaderFactory<Reader> toReaderFactory(CharsetDecoder charsetDecoder) {
			return getSource().toReaderFactory(charsetDecoder);
		}

		@Override
		default ReaderFactory<Reader> toReaderFactory(String charsetName) {
			return getSource().toReaderFactory(charsetName);
		}

		@Override
		default void transferTo(File dest) throws IOException, IllegalStateException {
			getSource().transferTo(dest);
		}

		@Override
		default void transferTo(Path dest) throws IOException, IllegalStateException {
			getSource().transferTo(dest);
		}
	}

	@RequiredArgsConstructor
	@Data
	public static class MappedInputStreamFactory<T extends InputStream, R extends Reader, W extends InputStreamFactory<T>>
			implements ReaderFactory<R>, InputStreamFactoryWrapper<T, W> {
		@NonNull
		private final W source;
		@NonNull
		private final Pipeline<? super T, ? extends R, ? extends IOException> pipeline;

		@Override
		public Channel<R, IOException> getReader() {
			return getSource().getInputStream().map(pipeline);
		}
	}

	@NonNull
	Channel<T, IOException> getInputStream();

	default <R extends Reader> ReaderFactory<R> map(
			@NonNull Pipeline<? super T, ? extends R, ? extends IOException> pipeline) {
		return new MappedInputStreamFactory<>(this, pipeline);
	}

	default byte[] readAllBytes() throws NoSuchElementException, IOException {
		return getInputStream().export().map(IOUtils::toByteArray).get();
	}

	default ReaderFactory<Reader> toReaderFactory() {
		return map(InputStreamReader::new);
	}

	default ReaderFactory<Reader> toReaderFactory(Charset charset) {
		return map((inputStream) -> new InputStreamReader(inputStream, charset));
	}

	default ReaderFactory<Reader> toReaderFactory(CharsetDecoder charsetDecoder) {
		return map((inputStream) -> new InputStreamReader(inputStream, charsetDecoder));
	}

	default ReaderFactory<Reader> toReaderFactory(String charsetName) {
		return map((inputStream) -> new InputStreamReader(inputStream, charsetName));
	}

	default void transferTo(File dest) throws IOException, IllegalStateException {
		getInputStream().export().ifPresent((is) -> FileUtils.copyInputStreamToFile(is, dest));
	}

	default void transferTo(Path dest) throws IOException, IllegalStateException {
		getInputStream().export().ifPresent((is) -> FileUtils.copyInputStreamToPath(is, dest));
	}
}