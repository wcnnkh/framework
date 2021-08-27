package io.basc.framework.microsoft;

@FunctionalInterface
public interface RowCallback {
	void processRow(int sheetIndex, int rowIndex, String[] contents);
}
