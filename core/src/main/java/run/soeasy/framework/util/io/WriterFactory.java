package run.soeasy.framework.util.io;

import java.io.IOException;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.util.function.Pipeline;
import run.soeasy.framework.util.function.Wrapper;

@FunctionalInterface
public interface WriterFactory<T extends Writer> {
	@FunctionalInterface
	public static interface WriterFactoryWrapper<T extends Writer, W extends WriterFactory<T>>
			extends WriterFactory<T>, Wrapper<W> {
		@Override
		default Pipeline<T, IOException> getWriterPipeline() {
			return getSource().getWriterPipeline();
		}
	}

	@NonNull
	Pipeline<T, IOException> getWriterPipeline();
}
