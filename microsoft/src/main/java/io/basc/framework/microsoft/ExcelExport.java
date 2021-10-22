package io.basc.framework.microsoft;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.util.page.Page;
import io.basc.framework.util.page.Pages;
import io.basc.framework.util.stream.ConsumerProcessor;
import io.basc.framework.util.stream.Processor;

public interface ExcelExport extends Flushable, Closeable {
	void append(Collection<String> contents) throws IOException;

	default void append(String... contents) throws IOException {
		append(Arrays.asList(contents));
	}

	default <T, E extends Throwable> void appendAll(Collection<? extends T> rows,
			Processor<T, Collection<?>, E> processor) throws IOException, E {
		for (T obj : rows) {
			Collection<?> cols = processor.process(obj);
			if (cols == null) {
				continue;
			}

			List<String> values = cols.stream().map((v) -> v == null ? null : String.valueOf(v))
					.collect(Collectors.toList());
			append(values);
		}
		flush();
	}

	/**
	 * @param <T>
	 * @param <E>
	 * @param pages
	 * @param rowsProcessor
	 * @param afterProcess 写入成功后执行
	 * @throws IOException
	 * @throws E
	 */
	default <T, E extends Throwable> void appendAll(Pages<T> pages, Processor<T, Collection<?>, E> rowsProcessor, ConsumerProcessor<Page<T>, E> afterProcess)
			throws IOException, E {
		appendAll(pages.rows(), rowsProcessor);
		afterProcess.process(pages);
		while (pages.hasNext()) {
			Page<T> page = pages.next();
			appendAll(page.rows(), rowsProcessor);
			afterProcess.process(page);
		}
	}
}
