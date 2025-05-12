package run.soeasy.framework.core.io;

import java.io.IOException;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.Wrapper;
import run.soeasy.framework.core.function.stream.Source;

@FunctionalInterface
public interface WriterFactory<T extends Writer> {
	@FunctionalInterface
	public static interface WriterFactoryWrapper<T extends Writer, W extends WriterFactory<T>>
			extends WriterFactory<T>, Wrapper<W> {
		@Override
		default Source<T, IOException> getWriterPipeline() {
			return getSource().getWriterPipeline();
		}
	}

	@NonNull
	Source<T, IOException> getWriterPipeline();
}
