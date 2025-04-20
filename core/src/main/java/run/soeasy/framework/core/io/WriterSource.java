package run.soeasy.framework.core.io;

import java.io.IOException;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.exe.Pipeline;

@FunctionalInterface
public interface WriterSource<T extends Writer> extends WriterFactory<T> {
	public static interface WriterSourceWrapper<T extends Writer, W extends WriterSource<T>>
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

	T getWriter() throws IOException;

	@Override
	default @NonNull Pipeline<T, IOException> getWriterPipeline() {
		return Pipeline.forCloseable(this::getWriter);
	}

}
