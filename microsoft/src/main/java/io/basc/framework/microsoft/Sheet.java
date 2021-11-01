package io.basc.framework.microsoft;

import java.io.IOException;
import java.util.Iterator;

import io.basc.framework.util.AbstractIterator;
import io.basc.framework.util.stream.Cursor;

public interface Sheet {
	String getName();

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
	 * 获取有多少行
	 * 
	 * @return
	 */
	int getRows();

	default Cursor<String[]> stream() {
		Iterator<String[]> iterator = new AbstractIterator<String[]>() {
			private int index = 0;
			private int count = getRows();

			@Override
			public boolean hasNext() {
				return index < count;
			}

			@Override
			public String[] next() {
				try {
					return read(index++);
				} catch (IOException e) {
					throw new ExcelException(e);
				}
			}
		};
		return new Cursor<>(iterator, false);
	}
}
