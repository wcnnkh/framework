package run.soeasy.framework.core.io;

import java.io.IOException;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.Wrapper;
import run.soeasy.framework.core.exe.Pipeline;

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
