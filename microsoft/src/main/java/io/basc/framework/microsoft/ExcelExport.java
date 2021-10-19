package io.basc.framework.microsoft;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import io.basc.framework.util.page.Pages;
import io.basc.framework.util.stream.Processor;

public interface ExcelExport extends Flushable, Closeable {
	void append(Collection<String> contents) throws IOException;

	default void append(String... contents) throws IOException {
		append(Arrays.asList(contents));
	}

	default <T, E extends Throwable> void appendAll(Collection<? extends T> rows,
			Processor<T, Collection<String>, E> processor) throws IOException, E {
		for (T obj : rows) {
			Collection<String> cols = processor.process(obj);
			if (cols == null) {
				continue;
			}

			append(cols);
		}
		flush();
	}

	default <T, E extends Throwable> void appendAll(Pages<T> pages, Processor<T, Collection<String>, E> processor)
			throws IOException, E {
		appendAll(pages.rows(), processor);
		while (pages.hasNext()) {
			appendAll(pages.next().rows(), processor);
		}
	}
}
