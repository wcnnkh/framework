package run.soeasy.framework.core.io.pipeline;

import java.io.IOException;
import java.io.Writer;

import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.function.Pipeline;

@FunctionalInterface
public interface WriterFactoryWrapper<T extends Writer, W extends WriterFactory<T>>
		extends WriterFactory<T>, Wrapper<W> {
	@Override
	default Pipeline<T, IOException> getWriterPipeline() {
		return getSource().getWriterPipeline();
	}
}
