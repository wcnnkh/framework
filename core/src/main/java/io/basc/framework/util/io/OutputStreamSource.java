package io.basc.framework.util.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import io.basc.framework.util.function.Function;
import io.basc.framework.util.function.Pipeline;
import io.basc.framework.util.function.Wrapper;
import lombok.Data;
import lombok.NonNull;

@FunctionalInterface
public interface OutputStreamSource<T extends OutputStream> {

	public static interface CharsetOutputStreamSource<T extends OutputStream, W extends OutputStreamSource<T>>
			extends EncodeOutputStreamSource<T, Writer, W>, CharsetCapable {
		@Override
		default Function<? super T, ? extends Writer, ? extends IOException> getEncoder() {
			return (e) -> new OutputStreamWriter(e, getCharset());
		}
	}

	public static class DefaultEncodeOutputStreamSource<T extends OutputStream, W extends OutputStreamSource<T>>
			extends StandardEncodeOutputStreamSource<T, Writer, W> {

		public DefaultEncodeOutputStreamSource(@NonNull W source) {
			super(source, OutputStreamWriter::new);
		}

		public DefaultEncodeOutputStreamSource(@NonNull W source, @NonNull CharsetEncoder charsetEncoder) {
			super(source, (e) -> new OutputStreamWriter(e, charsetEncoder));
		}
	}

	public static interface EncodeOutputStreamSource<T extends OutputStream, R extends Writer, W extends OutputStreamSource<T>>
			extends OutputStreamSourceWrapper<T, W>, WriterSource<R> {
		Function<? super T, ? extends R, ? extends IOException> getEncoder();

		@Override
		default @NonNull Pipeline<R, IOException> getWriter() {
			return getSource().getOutputStream().map(getEncoder());
		}
	}

	@FunctionalInterface
	public static interface OutputStreamSourceWrapper<T extends OutputStream, W extends OutputStreamSource<T>>
			extends OutputStreamSource<T>, Wrapper<W> {
		@Override
		default Pipeline<T, IOException> getOutputStream() {
			return getSource().getOutputStream();
		}

		@Override
		default <R extends Writer> WriterSource<R> writer(
				@NonNull Function<? super T, ? extends R, ? extends IOException> pipeline) {
			return getSource().writer(pipeline);
		}

		@Override
		default WriterSource<Writer> toWriterSource() {
			return getSource().toWriterSource();
		}

		@Override
		default WriterSource<Writer> toWriterSource(Charset charset) {
			return getSource().toWriterSource(charset);
		}

		@Override
		default WriterSource<Writer> toWriterSource(CharsetEncoder charsetEncoder) {
			return getSource().toWriterSource(charsetEncoder);
		}

		@Override
		default WriterSource<Writer> toWriterSource(String charsetName) {
			return getSource().toWriterSource(charsetName);
		}
	}

	public static class StandardCharsetOutputStreamSource<T extends OutputStream, W extends OutputStreamSource<T>>
			extends StandardEncodeOutputStreamSource<T, Writer, W> implements CharsetOutputStreamSource<T, W> {
		private final Object charset;

		public StandardCharsetOutputStreamSource(@NonNull W source, Charset charset) {
			super(source, (e) -> new OutputStreamWriter(e, charset));
			this.charset = charset;
		}

		public StandardCharsetOutputStreamSource(@NonNull W source, String charsetName) {
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
			return CharsetOutputStreamSource.super.getCharsetName();
		}

	}

	@Data
	public static class StandardEncodeOutputStreamSource<T extends OutputStream, R extends Writer, W extends OutputStreamSource<T>>
			implements EncodeOutputStreamSource<T, R, W> {
		@NonNull
		private final W source;
		@NonNull
		private final Function<? super T, ? extends R, ? extends IOException> encoder;
	}

	@NonNull
	Pipeline<T, IOException> getOutputStream();

	default <R extends Writer> WriterSource<R> writer(
			@NonNull Function<? super T, ? extends R, ? extends IOException> pipeline) {
		return new StandardEncodeOutputStreamSource<>(this, pipeline);
	}

	default WriterSource<Writer> toWriterSource() {
		return new DefaultEncodeOutputStreamSource<>(this);
	}

	default WriterSource<Writer> toWriterSource(Charset charset) {
		return new StandardCharsetOutputStreamSource<>(this, charset);
	}

	default WriterSource<Writer> toWriterSource(CharsetEncoder charsetEncoder) {
		return new DefaultEncodeOutputStreamSource<>(this, charsetEncoder);
	}

	default WriterSource<Writer> toWriterSource(String charsetName) {
		return new StandardCharsetOutputStreamSource<>(this, charsetName);
	}
}
