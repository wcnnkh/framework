package io.basc.framework.util.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import io.basc.framework.util.Channel;
import io.basc.framework.util.Pipeline;
import io.basc.framework.util.Wrapper;
import lombok.Data;
import lombok.NonNull;

@FunctionalInterface
public interface OutputStreamFactory<T extends OutputStream> {
	@Data
	public static class MappedOutputStreamFactory<T extends OutputStream, R extends Writer, W extends OutputStreamFactory<T>>
			implements WriterFactory<R> {
		@NonNull
		private final W source;
		@NonNull
		private final Pipeline<? super T, ? extends R, ? extends IOException> pipeline;

		@Override
		public Channel<R, IOException> getWriter() {
			return source.getOutputStream().map(pipeline);
		}
	}

	@FunctionalInterface
	public static interface OutputStreamFactoryWrapper<T extends OutputStream, W extends OutputStreamFactory<T>>
			extends OutputStreamFactory<T>, Wrapper<W> {
		@Override
		default Channel<T, IOException> getOutputStream() {
			return getSource().getOutputStream();
		}

		@Override
		default <R extends Writer> WriterFactory<R> map(
				@NonNull Pipeline<? super T, ? extends R, ? extends IOException> pipeline) {
			return getSource().map(pipeline);
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

	@NonNull
	Channel<T, IOException> getOutputStream();

	default <R extends Writer> WriterFactory<R> map(
			@NonNull Pipeline<? super T, ? extends R, ? extends IOException> pipeline) {
		return new MappedOutputStreamFactory<>(this, pipeline);
	}

	default WriterFactory<Writer> toWriterFactory() {
		return map(OutputStreamWriter::new);
	}

	default WriterFactory<Writer> toWriterFactory(Charset charset) {
		return map((os) -> new OutputStreamWriter(os, charset));
	}

	default WriterFactory<Writer> toWriterFactory(CharsetEncoder charsetEncoder) {
		return map((os) -> new OutputStreamWriter(os, charsetEncoder));
	}

	default WriterFactory<Writer> toWriterFactory(String charsetName) {
		return map((os) -> new OutputStreamWriter(os, charsetName));
	}
}
