package run.soeasy.framework.core.io.pipeline;

import java.io.IOException;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;

@FunctionalInterface
public interface WriterSource<T extends Writer> extends WriterFactory<T> {
	T getWriter() throws IOException;

	@Override
	default @NonNull Pipeline<T, IOException> getWriterPipeline() {
		return Pipeline.forCloseable(this::getWriter);
	}

}
