package run.soeasy.framework.io;

import java.io.IOException;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;

@FunctionalInterface
public interface WriterFactory<T extends Writer> {
	default Writer getWriter() throws IOException {
		return new WriterPipeline(getWriterPipeline());
	}

	@NonNull
	Pipeline<T, IOException> getWriterPipeline();
}
