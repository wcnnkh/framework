package io.basc.framework.microsoft;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.page.Pageable;
import io.basc.framework.util.page.Pageables;
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
	
	default <T, E extends Throwable, C, P extends Pageables<C, T>> void appendAll(P pages, Processor<T, Collection<?>, E> rowsProcessor) throws IOException, E{
		appendAll(pages, rowsProcessor, null);
	}
	
	/**
	 * @param <T>
	 * @param <E>
	 * @param pages
	 * @param rowsProcessor
	 * @param afterProcess  写入成功后执行
	 * @throws IOException
	 * @throws E
	 */
	default <T, E extends Throwable, C, P extends Pageables<C, T>> void appendAll(P pages, Processor<T, Collection<?>, E> rowsProcessor,
			@Nullable ConsumerProcessor<Pageable<C, T>, E> afterProcess) throws IOException, E {
		Stream<? extends Pageable<C, T>> stream = pages.pages();
		try {
			Iterator<? extends Pageable<C, T>> iterator = stream.iterator();
			while (iterator.hasNext()) {
				Pageable<C, T> page = iterator.next();
				appendAll(page.rows(), rowsProcessor);
				if(afterProcess != null) {
					afterProcess.process(page);
				}
			}
		} finally {
			stream.close();
		}
	}
}
