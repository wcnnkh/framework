package io.basc.framework.microsoft;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

public interface ExcelExport extends Flushable, Closeable {
	boolean isEmpty();

	void put(Collection<String> contents) throws ExcelException, IOException;

	default void put(String... contents) throws ExcelException, IOException {
		put(Arrays.asList(contents));
	}

	default void putAll(Iterator<? extends String[]> iterator) throws ExcelException, IOException {
		while (iterator.hasNext()) {
			String[] values = iterator.next();
			put(values);
		}
	}

	default void putAll(Stream<? extends String[]> stream) throws ExcelException, IOException {
		putAll(stream.iterator());
	}
}
