package run.soeasy.framework.util.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.util.function.Function;
import run.soeasy.framework.util.function.Pipeline;

public interface OutputFactory<O extends OutputStream, W extends Writer>
		extends OutputStreamFactory<O>, WriterFactory<W> {
	public static interface OutputFactoryWrapper<O extends OutputStream, E extends Writer, W extends OutputFactory<O, E>>
			extends OutputFactory<O, E>, OutputStreamFactoryWrapper<O, W>, WriterFactoryWrapper<E, W> {
		@Override
		default boolean isEncoded() {
			return getSource().isEncoded();
		}
	}

	@Override
	boolean isEncoded();

	@RequiredArgsConstructor
	@Getter
	public static class DefaultOutputFactory<O extends OutputStream, S extends OutputStreamFactory<? extends O>, W extends Writer, T extends WriterFactory<? extends W>>
			implements OutputFactory<O, W> {
		protected final S outputStreamFactory;
		protected final T writerFactory;

		@Override
		public @NonNull Pipeline<O, IOException> getOutputStreamPipeline() {
			return outputStreamFactory == null ? Pipeline.empty()
					: outputStreamFactory.getOutputStreamPipeline().map(Function.identity());
		}

		@Override
		public @NonNull Pipeline<W, IOException> getWriterPipeline() {
			return writerFactory == null ? Pipeline.empty()
					: writerFactory.getWriterPipeline().map(Function.identity());
		}

		@Override
		public boolean isEncoded() {
			return writerFactory != null;
		}

		@Override
		public OutputFactory<O, Writer> encode() {
			if (writerFactory != null) {
				return new DefaultOutputFactory<>(outputStreamFactory, writerFactory);
			}
			return OutputFactory.super.encode();
		}
	}

	public static <O extends OutputStream, W extends Writer> OutputFactory<O, W> forFactory(
			OutputStreamFactory<? extends O> outputStreamFactory, WriterFactory<? extends W> writerFactory) {
		return new DefaultOutputFactory<>(outputStreamFactory, writerFactory);
	}
}
