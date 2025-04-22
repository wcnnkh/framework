package run.soeasy.framework.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.Wrapper;
import run.soeasy.framework.core.exe.Function;
import run.soeasy.framework.core.function.Pipeline;

@FunctionalInterface
public interface OutputStreamFactory<T extends OutputStream> {

	public static interface CharsetOutputStreamFactory<T extends OutputStream, W extends OutputStreamFactory<T>>
			extends EncodedOutputStreamFactory<T, Writer, W>, CharsetCapable {
		@Override
		default Function<? super T, ? extends Writer, ? extends IOException> getEncoder() {
			return (e) -> new OutputStreamWriter(e, getCharset());
		}
	}

	public static class DefaultEncodedOutputStreamFactory<T extends OutputStream, W extends OutputStreamFactory<T>>
			extends StandardEncodedOutputStreamFactory<T, Writer, W> {

		public DefaultEncodedOutputStreamFactory(@NonNull W source) {
			super(source, OutputStreamWriter::new);
		}

		public DefaultEncodedOutputStreamFactory(@NonNull W source, @NonNull CharsetEncoder charsetEncoder) {
			super(source, (e) -> new OutputStreamWriter(e, charsetEncoder));
		}
	}

	public static interface EncodedOutputStreamFactory<T extends OutputStream, R extends Writer, W extends OutputStreamFactory<T>>
			extends OutputStreamFactoryWrapper<T, W>, OutputFactory<T, R> {
		Function<? super T, ? extends R, ? extends IOException> getEncoder();

		@Override
		default @NonNull Pipeline<R, IOException> getWriterPipeline() {
			return getSource().getOutputStreamPipeline().map(getEncoder());
		}

		@Override
		default boolean isEncoded() {
			return true;
		}
	}

	@FunctionalInterface
	public static interface OutputStreamFactoryWrapper<T extends OutputStream, W extends OutputStreamFactory<T>>
			extends OutputStreamFactory<T>, Wrapper<W> {
		@Override
		default Pipeline<T, IOException> getOutputStreamPipeline() {
			return getSource().getOutputStreamPipeline();
		}

		@Override
		default <R extends Writer> OutputFactory<T, R> encode(
				@NonNull Function<? super T, ? extends R, ? extends IOException> pipeline) {
			return getSource().encode(pipeline);
		}

		@Override
		default OutputFactory<T, Writer> encode() {
			return getSource().encode();
		}

		@Override
		default OutputFactory<T, Writer> encode(Charset charset) {
			return getSource().encode(charset);
		}

		@Override
		default OutputFactory<T, Writer> encode(CharsetEncoder charsetEncoder) {
			return getSource().encode(charsetEncoder);
		}

		@Override
		default OutputFactory<T, Writer> encode(String charsetName) {
			return getSource().encode(charsetName);
		}

		@Override
		default boolean isEncoded() {
			return getSource().isEncoded();
		}
	}

	public static class StandardCharsetOutputStreamFactory<T extends OutputStream, W extends OutputStreamFactory<T>>
			extends StandardEncodedOutputStreamFactory<T, Writer, W> implements CharsetOutputStreamFactory<T, W> {
		private final Object charset;

		public StandardCharsetOutputStreamFactory(@NonNull W source, Charset charset) {
			super(source, (e) -> new OutputStreamWriter(e, charset));
			this.charset = charset;
		}

		public StandardCharsetOutputStreamFactory(@NonNull W source, String charsetName) {
			super(source, (e) -> new OutputStreamWriter(e, charsetName));
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
			return CharsetOutputStreamFactory.super.getCharsetName();
		}

	}

	@Data
	public static class StandardEncodedOutputStreamFactory<T extends OutputStream, R extends Writer, W extends OutputStreamFactory<T>>
			implements EncodedOutputStreamFactory<T, R, W> {
		@NonNull
		private final W source;
		@NonNull
		private final Function<? super T, ? extends R, ? extends IOException> encoder;
	}

	@NonNull
	Pipeline<T, IOException> getOutputStreamPipeline();

	default boolean isEncoded() {
		return false;
	}

	default <R extends Writer> OutputFactory<T, R> encode(
			@NonNull Function<? super T, ? extends R, ? extends IOException> pipeline) {
		return new StandardEncodedOutputStreamFactory<>(this, pipeline);
	}

	default OutputFactory<T, Writer> encode() {
		return new DefaultEncodedOutputStreamFactory<>(this);
	}

	default OutputFactory<T, Writer> encode(Charset charset) {
		return new StandardCharsetOutputStreamFactory<>(this, charset);
	}

	default OutputFactory<T, Writer> encode(CharsetEncoder charsetEncoder) {
		return new DefaultEncodedOutputStreamFactory<>(this, charsetEncoder);
	}

	default OutputFactory<T, Writer> encode(String charsetName) {
		return new StandardCharsetOutputStreamFactory<>(this, charsetName);
	}
}
