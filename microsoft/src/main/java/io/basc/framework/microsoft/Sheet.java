package io.basc.framework.microsoft;

import java.io.IOException;

public interface Sheet {
	String getName();

	String[] read(int rowIndex) throws IOException, ExcelException;

	String read(int rowIndex, int colIndex) throws IOException, ExcelException;

	int getRows();
}
