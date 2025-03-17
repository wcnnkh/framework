package run.soeasy.framework.util.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.util.function.Function;
import run.soeasy.framework.util.function.Pipeline;
import run.soeasy.framework.util.function.Wrapper;

@FunctionalInterface
public interface OutputStreamFactory<T extends OutputStream> {

	public static interface CharsetOutputStreamFactory<T extends OutputStream, W extends OutputStreamFactory<T>>
			extends EncodeOutputStreamFactory<T, Writer, W>, CharsetCapable {
		@Override
		default Function<? super T, ? extends Writer, ? extends IOException> getEncoder() {
			return (e) -> new OutputStreamWriter(e, getCharset());
		}
	}

	public static class DefaultEncodeOutputStreamFactory<T extends OutputStream, W extends OutputStreamFactory<T>>
			extends StandardEncodeOutputStreamFactory<T, Writer, W> {

		public DefaultEncodeOutputStreamFactory(@NonNull W source) {
			super(source, OutputStreamWriter::new);
		}

		public DefaultEncodeOutputStreamFactory(@NonNull W source, @NonNull CharsetEncoder charsetEncoder) {
			super(source, (e) -> new OutputStreamWriter(e, charsetEncoder));
		}
	}

	public static interface EncodeOutputStreamFactory<T extends OutputStream, R extends Writer, W extends OutputStreamFactory<T>>
			extends OutputStreamFactoryWrapper<T, W>, WriterFactory<R> {
		Function<? super T, ? extends R, ? extends IOException> getEncoder();

		@Override
		default @NonNull Pipeline<R, IOException> getWriterPipeline() {
			return getSource().getOutputStreamPipeline().map(getEncoder());
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
		default <R extends Writer> WriterFactory<R> toWriterFactory(
				@NonNull Function<? super T, ? extends R, ? extends IOException> pipeline) {
			return getSource().toWriterFactory(pipeline);
		}

		@Override
		default WriterFactory<Writer> toWriterFactory() {
			return getSource().toWriterFactory();
		}

		@Override
		default WriterFactory<Writer> toWriterFactory(Charset charset) {
			return getSource().toWriterFactory(charset);
		}

		@Override
		default WriterFactory<Writer> toWriterFactory(CharsetEncoder charsetEncoder) {
			return getSource().toWriterFactory(charsetEncoder);
		}

		@Override
		default WriterFactory<Writer> toWriterFactory(String charsetName) {
			return getSource().toWriterFactory(charsetName);
		}
	}

	public static class StandardCharsetOutputStreamFactory<T extends OutputStream, W extends OutputStreamFactory<T>>
			extends StandardEncodeOutputStreamFactory<T, Writer, W> implements CharsetOutputStreamFactory<T, W> {
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
	public static class StandardEncodeOutputStreamFactory<T extends OutputStream, R extends Writer, W extends OutputStreamFactory<T>>
			implements EncodeOutputStreamFactory<T, R, W> {
		@NonNull
		private final W source;
		@NonNull
		private final Function<? super T, ? extends R, ? extends IOException> encoder;
	}

	@NonNull
	Pipeline<T, IOException> getOutputStreamPipeline();

	default <R extends Writer> WriterFactory<R> toWriterFactory(
			@NonNull Function<? super T, ? extends R, ? extends IOException> pipeline) {
		return new StandardEncodeOutputStreamFactory<>(this, pipeline);
	}

	default WriterFactory<Writer> toWriterFactory() {
		return new DefaultEncodeOutputStreamFactory<>(this);
	}

	default WriterFactory<Writer> toWriterFactory(Charset charset) {
		return new StandardCharsetOutputStreamFactory<>(this, charset);
	}

	default WriterFactory<Writer> toWriterFactory(CharsetEncoder charsetEncoder) {
		return new DefaultEncodeOutputStreamFactory<>(this, charsetEncoder);
	}

	default WriterFactory<Writer> toWriterFactory(String charsetName) {
		return new StandardCharsetOutputStreamFactory<>(this, charsetName);
	}
}
