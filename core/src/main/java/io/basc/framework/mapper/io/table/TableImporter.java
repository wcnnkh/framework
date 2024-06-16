package io.basc.framework.mapper.io.table;

import java.io.IOException;

public interface TableImporter extends RowImporter {
	/**
	 * 获取列数
	 * 
	 * @return
	 */
	int getNumberOfColumns();

	default Row readRow(int rowIndex) throws IOException {
		Column[] columns = new Column[getNumberOfColumns()];
		for (int colIndex = 0; colIndex < columns.length; colIndex++) {
			columns[colIndex] = readColumn(rowIndex, colIndex);
		}

		SimpleRow row = new SimpleRow(columns);
		row.setPositionIndex(rowIndex);
		return row;
	}

	Column readColumn(int rowIndex, int colIndex) throws IOException;
}
