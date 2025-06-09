package run.soeasy.framework.core.io.pipeline;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

@RequiredArgsConstructor
@Getter
public class DefaultOutputFactory<O extends OutputStream, S extends OutputStreamFactory<? extends O>, W extends Writer, T extends WriterFactory<? extends W>>
		implements OutputFactory<O, W> {
	protected final S outputStreamFactory;
	protected final T writerFactory;

	@Override
	public @NonNull Pipeline<O, IOException> getOutputStreamPipeline() {
		return outputStreamFactory == null ? Pipeline.empty()
				: outputStreamFactory.getOutputStreamPipeline().map(ThrowingFunction.identity());
	}

	@Override
	public @NonNull Pipeline<W, IOException> getWriterPipeline() {
		return writerFactory == null ? Pipeline.empty()
				: writerFactory.getWriterPipeline().map(ThrowingFunction.identity());
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