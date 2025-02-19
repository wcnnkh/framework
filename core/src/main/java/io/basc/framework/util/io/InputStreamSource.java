package io.basc.framework.util.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import io.basc.framework.util.function.Consumer;
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
		default <R extends Reader> ReaderSource<R> toReaderSource(
				@NonNull Function<? super T, ? extends R, ? extends IOException> pipeline) {
			return getSource().toReaderSource(pipeline);
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

		@Override
		default <E extends Throwable> void exportByteBuffer(Consumer<? super ByteBuffer, ? extends E> consumer)
				throws IOException, E {
			getSource().exportByteBuffer(consumer);
		}

		@Override
		default <R extends OutputStream> void transferTo(@NonNull OutputStreamSource<? extends R> dest)
				throws IOException {
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

	default <R extends Reader> ReaderSource<R> toReaderSource(
			@NonNull Function<? super T, ? extends R, ? extends IOException> pipeline) {
		return new StandardDecodeInputStreamSource<>(this, pipeline);
	}

	default byte[] readAllBytes() throws NoSuchElementException, IOException {
		return getInputStream().map(IOUtils::toByteArray).get();
	}

	default <E extends Throwable> void exportByteBuffer(Consumer<? super ByteBuffer, ? extends E> consumer)
			throws IOException, E {
	}

	default ReaderSource<Reader> toReaderSource() {
		return new DefaultDecodeInputStreamSource<>(this);
	}

	default ReaderSource<Reader> toReaderSource(@NonNull Charset charset) {
		return new StandardCharsetInputStreamSource<>(this, charset);
	}

	default ReaderSource<Reader> toReaderSource(@NonNull CharsetDecoder charsetDecoder) {
		return new DefaultDecodeInputStreamSource<>(this, charsetDecoder);
	}

	default ReaderSource<Reader> toReaderSource(@NonNull String charsetName) {
		return new StandardCharsetInputStreamSource<>(this, charsetName);
	}

	default void transferTo(@NonNull File dest) throws IOException, IllegalStateException {
		getInputStream().optional().ifPresent((is) -> FileUtils.copyInputStreamToFile(is, dest));
	}

	default void transferTo(@NonNull Path dest) throws IOException, IllegalStateException {
		getInputStream().optional().ifPresent((is) -> FileUtils.copyInputStreamToPath(is, dest));
	}

	default <R extends OutputStream> void transferTo(@NonNull OutputStreamSource<? extends R> dest) throws IOException {
		getInputStream().optional()
				.ifPresent((is) -> dest.getOutputStream().optional().ifPresent((os) -> IOUtils.copy(is, os)));
	}
}