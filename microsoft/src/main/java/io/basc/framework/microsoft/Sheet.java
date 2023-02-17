package io.basc.framework.microsoft;

import java.io.IOException;
import java.math.BigInteger;

import io.basc.framework.util.Cursor;
import io.basc.framework.util.PositionIterator;
import io.basc.framework.util.ResultSet;

public interface Sheet extends ResultSet<String[]> {
	String getName();

	int getRows();

	/**
	 * 读取指定行
	 * 
	 * @param rowIndex 从0开始
	 * @return
	 * @throws IOException
	 * @throws ExcelException
	 */
	String[] read(int rowIndex) throws IOException, ExcelException;

	/**
	 * 读取指定行，指定列
	 * 
	 * @param rowIndex 从0开始
	 * @param colIndex 从0开始
	 * @return
	 * @throws IOException
	 * @throws ExcelException
	 */
	String read(int rowIndex, int colIndex) throws IOException, ExcelException;

	/**
	 * 如果支持流读取应该重写此方法
	 */
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
