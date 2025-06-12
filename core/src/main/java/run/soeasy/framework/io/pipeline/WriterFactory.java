package run.soeasy.framework.io.pipeline;

import java.io.IOException;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;

@FunctionalInterface
public interface WriterFactory<T extends Writer> {
	@NonNull
	Pipeline<T, IOException> getWriterPipeline();
}
