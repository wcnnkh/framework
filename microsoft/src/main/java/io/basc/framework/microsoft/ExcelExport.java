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

	/**
	 * 添加一行数据
	 * 
	 * @param contents
	 * @return this
	 * @throws ExcelException
	 * @throws IOException
	 */
	void put(Collection<String> contents) throws ExcelException, IOException;

	/**
	 * 添加一行数据
	 * 
	 * @param contents
	 * @return this
	 * @throws ExcelException
	 * @throws IOException
	 */
	default void put(String... contents) throws ExcelException, IOException {
		put(Arrays.asList(contents));
	}

	/**
	 * 添加一批数据
	 * 
	 * @param iterator
	 * @return this
	 * @throws ExcelException
	 * @throws IOException
	 */
	default void putAll(Iterator<? extends String[]> iterator) throws ExcelException, IOException {
		while (iterator.hasNext()) {
			String[] values = iterator.next();
			put(values);
		}
	}

	/**
	 * 添加一批数据
	 * 
	 * @param stream
	 * @return this
	 * @throws ExcelException
	 * @throws IOException
	 */
	default void putAll(Stream<? extends String[]> stream) throws ExcelException, IOException {
		putAll(stream.iterator());
	}
}
