package io.basc.framework.microsoft;

import java.io.IOException;
import java.math.BigInteger;

import io.basc.framework.util.Cursor;
import io.basc.framework.util.PositionIterator;
import io.basc.framework.util.ResultSet;

public interface Sheet extends ResultSet<String[]> {
	String getName();

	int getRows();

	String[] read(int rowIndex) throws IOException, ExcelException;

	String read(int rowIndex, int colIndex) throws IOException, ExcelException;

	default Cursor<String[]> iterator() {
		PositionIterator<String[]> iterator = new PositionIterator<>(BigInteger.valueOf(getRows()), (e) -> {
			try {
				return read(e.intValue());
			} catch (IOException ex) {
				throw new ExcelException(ex);
			}
		});
		return Cursor.of(iterator);
	}
}
