package io.basc.framework.microsoft;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.util.Elements;
import io.basc.framework.util.PositionIterator;
import io.basc.framework.util.Streams;

public interface Sheet extends Elements<String[]> {
	String getName();

	int getRows();

	String[] read(int rowIndex) throws IOException, ExcelException;

	String read(int rowIndex, int colIndex) throws IOException, ExcelException;

	default Iterator<String[]> iterator() {
		return new PositionIterator<>(BigInteger.valueOf(getRows()), (e) -> {
			try {
				return read(e.intValue());
			} catch (IOException ex) {
				throw new ExcelException(ex);
			}
		});
	}

	/**
	 * 默认使用{@link #iterator()}
	 */
	@Override
	default Stream<String[]> stream() {
		return Streams.stream(spliterator());
	}
}
