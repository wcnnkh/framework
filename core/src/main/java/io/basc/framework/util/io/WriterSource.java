package io.basc.framework.util.io;

import java.io.IOException;
import java.io.Writer;

import io.basc.framework.util.function.Pipeline;
import io.basc.framework.util.function.Wrapper;
import lombok.NonNull;

@FunctionalInterface
public interface WriterSource<T extends Writer> {
	@FunctionalInterface
	public static interface WriterSourceWrapper<T extends Writer, W extends WriterSource<T>>
			extends WriterSource<T>, Wrapper<W> {
		@Override
		default Pipeline<T, IOException> getWriter() {
			return getSource().getWriter();
		}
	}

	@NonNull
	Pipeline<T, IOException> getWriter();
}
