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

import io.basc.framework.util.function.Function;
import io.basc.framework.util.function.Pipeline;
import io.basc.framework.util.function.Wrapper;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@FunctionalInterface
public interface InputStreamSource<T extends InputStream> {
	public static interface CharsetInputStreamSource<T extends InputStream, W extends InputStreamSource<T>>
			extends DecodeInputStreamSource<T, Reader, W>, CharsetCapable {
		@Override
		default Function<? super T, ? extends Reader, ? extends IOException> getDecoder() {
			return (e) -> new InputStreamReader(e, getCharset());
		}
	}

	public static interface DecodeInputStreamSource<T extends InputStream, R extends Reader, W extends InputStreamSource<T>>
			extends ReaderSource<R>, InputStreamSourceWrapper<T, W> {

		Function<? super T, ? extends R, ? extends IOException> getDecoder();

		@Override
		default @NonNull Pipeline<R, IOException> getReader() {
			return getSource().getInputStream().map(getDecoder());
		}
	}

	public static class DefaultDecodeInputStreamSource<T extends InputStream, W extends InputStreamSource<T>>
			extends StandardDecodeInputStreamSource<T, Reader, W> {

		public DefaultDecodeInputStreamSource(@NonNull W source) {
			super(source, InputStreamReader::new);
		}

		public DefaultDecodeInputStreamSource(@NonNull W source, @NonNull CharsetDecoder charsetDecoder) {
			super(source, (e) -> new InputStreamReader(e, charsetDecoder));
		}
	}

	@FunctionalInterface
	public static interface InputStreamSourceWrapper<T extends InputStream, W extends InputStreamSource<T>>
			extends InputStreamSource<T>, Wrapper<W> {
		@Override
		default Pipeline<T, IOException> getInputStream() {
			return getSource().getInputStream();
		}

		@Override
		default <R extends Reader> ReaderSource<R> reader(
				@NonNull Function<? super T, ? extends R, ? extends IOException> pipeline) {
			return getSource().reader(pipeline);
		}

		@Override
		default byte[] readAllBytes() throws NoSuchElementException, IOException {
			return getSource().readAllBytes();
		}

		@Override
		default ReaderSource<Reader> toReaderSource() {
			return getSource().toReaderSource();
		}

		@Override
		default ReaderSource<Reader> toReaderSource(Charset charset) {
			return getSource().toReaderSource(charset);
		}

		@Override
		default ReaderSource<Reader> toReaderSource(CharsetDecoder charsetDecoder) {
			return getSource().toReaderSource(charsetDecoder);
		}

		@Override
		default ReaderSource<Reader> toReaderSource(String charsetName) {
			return getSource().toReaderSource(charsetName);
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

	public static class StandardCharsetInputStreamSource<T extends InputStream, W extends InputStreamSource<T>>
			extends StandardDecodeInputStreamSource<T, Reader, W> implements CharsetInputStreamSource<T, W> {
		private final Object charset;

		public StandardCharsetInputStreamSource(@NonNull W source, Charset charset) {
			super(source, (e) -> new InputStreamReader(e, charset));
			this.charset = charset;
		}

		public StandardCharsetInputStreamSource(@NonNull W source, String charsetName) {
			super(source, (e) -> new InputStreamReader(e, charsetName));
			this.charset = charsetName;
		}

		@Override
		public Charset getCharset() {
			if (charset instanceof Charset) {
				return (Charset) charset;
			}
			return Charset.forName(String.valueOf(charset));
		}

		@Override
		public String getCharsetName() {
			if (charset instanceof String) {
				return (String) charset;
			}
			return CharsetInputStreamSource.super.getCharsetName();
		}

	}

	@RequiredArgsConstructor
	@Data
	public static class StandardDecodeInputStreamSource<T extends InputStream, R extends Reader, W extends InputStreamSource<T>>
			implements DecodeInputStreamSource<T, R, W> {
		@NonNull
		private final W source;
		@NonNull
		private final Function<? super T, ? extends R, ? extends IOException> decoder;
	}

	@NonNull
	Pipeline<T, IOException> getInputStream();

	default <R extends Reader> ReaderSource<R> reader(
			@NonNull Function<? super T, ? extends R, ? extends IOException> pipeline) {
		return new StandardDecodeInputStreamSource<>(this, pipeline);
	}

	default byte[] readAllBytes() throws NoSuchElementException, IOException {
		return getInputStream().map(IOUtils::toByteArray).finish().get();
	}

	default ReaderSource<Reader> toReaderSource() {
		return new DefaultDecodeInputStreamSource<>(this);
	}

	default ReaderSource<Reader> toReaderSource(Charset charset) {
		return new StandardCharsetInputStreamSource<>(this, charset);
	}

	default ReaderSource<Reader> toReaderSource(CharsetDecoder charsetDecoder) {
		return new DefaultDecodeInputStreamSource<>(this, charsetDecoder);
	}

	default ReaderSource<Reader> toReaderSource(String charsetName) {
		return new StandardCharsetInputStreamSource<>(this, charsetName);
	}

	default void transferTo(File dest) throws IOException, IllegalStateException {
		getInputStream().export().ifPresent((is) -> FileUtils.copyInputStreamToFile(is, dest));
	}

	default void transferTo(Path dest) throws IOException, IllegalStateException {
		getInputStream().export().ifPresent((is) -> FileUtils.copyInputStreamToPath(is, dest));
	}
}