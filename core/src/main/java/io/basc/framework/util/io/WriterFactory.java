package io.basc.framework.util.io;

import java.io.IOException;
import java.io.Writer;

import io.basc.framework.util.Pipeline;
import io.basc.framework.util.Wrapper;
import lombok.NonNull;

@FunctionalInterface
public interface WriterFactory<T extends Writer> {
	@FunctionalInterface
	public static interface WriterFactoryWrapper<T extends Writer, W extends WriterFactory<T>>
			extends WriterFactory<T>, Wrapper<W> {
		@Override
		default Pipeline<T, IOException> getWriter() {
			return getSource().getWriter();
		}
	}

	@NonNull
	Pipeline<T, IOException> getWriter();
}
