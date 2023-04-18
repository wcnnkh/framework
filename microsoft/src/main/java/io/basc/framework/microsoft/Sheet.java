package io.basc.framework.microsoft;

import java.io.IOException;
import java.math.BigInteger;

import io.basc.framework.util.Elements;
import io.basc.framework.util.PositionIterator;

public interface Sheet {
	String getName();

	int getRows();

	String[] read(int rowIndex) throws IOException, ExcelException;

	String read(int rowIndex, int colIndex) throws IOException, ExcelException;

	default Elements<String[]> getElements() {
		return Elements.of(() -> new PositionIterator<>(BigInteger.valueOf(getRows()), (e) -> {
			try {
				return read(e.intValue());
			} catch (IOException ex) {
				throw new ExcelException(ex);
			}
		}));
	}
}
