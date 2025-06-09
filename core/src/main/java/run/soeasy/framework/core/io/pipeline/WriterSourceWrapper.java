package run.soeasy.framework.core.io.pipeline;

import java.io.IOException;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;

public interface WriterSourceWrapper<T extends Writer, W extends WriterSource<T>>
		extends WriterSource<T>, WriterFactoryWrapper<T, W> {
	@Override
	default T getWriter() throws IOException {
		return getSource().getWriter();
	}

	@Override
	default @NonNull Pipeline<T, IOException> getWriterPipeline() {
		return getSource().getWriterPipeline();
	}
}