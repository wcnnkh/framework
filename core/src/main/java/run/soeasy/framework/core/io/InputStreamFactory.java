package run.soeasy.framework.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.Wrapper;
import run.soeasy.framework.core.function.Function;
import run.soeasy.framework.core.function.Pipeline;

@FunctionalInterface
public interface InputStreamFactory<T extends InputStream> {
	public static interface CharsetInputStreamFactory<T extends InputStream, W extends InputStreamFactory<T>>
			extends DecodedInputStreamFactory<T, Reader, W>, CharsetCapable {
		@Override
		default Function<? super T, ? extends Reader, ? extends IOException> getDecoder() {
			return (e) -> new InputStreamReader(e, getCharset());
		}
	}

	public static interface DecodedInputStreamFactory<T extends InputStream, R extends Reader, W extends InputStreamFactory<T>>
			extends InputFactory<T, R>, InputStreamFactoryWrapper<T, W> {

		Function<? super T, ? extends R, ? extends IOException> getDecoder();

		@Override
		default @NonNull Pipeline<R, IOException> getReaderPipeline() {
			return getSource().getInputStreamPipeline().map(getDecoder());
		}

		@Override
		default boolean isDecoded() {
			return true;
		}
	}

	public static class DefaultDecodedInputStreamFactory<T extends InputStream, W extends InputStreamFactory<T>>
			extends StandardDecodedInputStreamFactory<T, Reader, W> {

		public DefaultDecodedInputStreamFactory(@NonNull W source) {
			super(source, InputStreamReader::new);
		}

		public DefaultDecodedInputStreamFactory(@NonNull W source, @NonNull CharsetDecoder charsetDecoder) {
			super(source, (e) -> new InputStreamReader(e, charsetDecoder));
		}
	}

	@FunctionalInterface
	public static interface InputStreamFactoryWrapper<T extends InputStream, W extends InputStreamFactory<T>>
			extends InputStreamFactory<T>, Wrapper<W> {
		@Override
		default Pipeline<T, IOException> getInputStreamPipeline() {
			return getSource().getInputStreamPipeline();
		}

		@Override
		default <R extends Reader> InputFactory<T, R> decode(
				@NonNull Function<? super T, ? extends R, ? extends IOException> pipeline) {
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

	public static class StandardCharsetInputStreamFactory<T extends InputStream, W extends InputStreamFactory<T>>
			extends StandardDecodedInputStreamFactory<T, Reader, W> implements CharsetInputStreamFactory<T, W> {
		private final Object charset;

		public StandardCharsetInputStreamFactory(@NonNull W source, Charset charset) {
			super(source, (e) -> new InputStreamReader(e, charset));
			this.charset = charset;
		}

		public StandardCharsetInputStreamFactory(@NonNull W source, String charsetName) {
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
			return CharsetInputStreamFactory.super.getCharsetName();
		}

	}

	@RequiredArgsConstructor
	@Getter
	public static class StandardDecodedInputStreamFactory<T extends InputStream, R extends Reader, W extends InputStreamFactory<T>>
			implements DecodedInputStreamFactory<T, R, W> {
		@NonNull
		private final W source;
		@NonNull
		private final Function<? super T, ? extends R, ? extends IOException> decoder;
	}

	@NonNull
	Pipeline<T, IOException> getInputStreamPipeline();

	default boolean isDecoded() {
		return false;
	}

	default <R extends Reader> InputFactory<T, R> decode(
			@NonNull Function<? super T, ? extends R, ? extends IOException> pipeline) {
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