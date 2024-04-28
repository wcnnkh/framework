package io.basc.framework.excel;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.transfer.Exporter;
import io.basc.framework.value.Value;

public interface Sheet extends SheetContext, ExcelImporter {
	/**
	 * 获取行数
	 * 
	 * @return
	 */
	int getNumberOfRows();

	/**
	 * 获取列数
	 * 
	 * @return
	 */
	int getNumberOfColumns();

	default Cell[] read(int rowIndex) throws IOException, ExcelException {
		Cell[] contents = new Cell[getNumberOfColumns()];
		for (int colIndex = 0; colIndex < contents.length; colIndex++) {
			contents[colIndex] = read(rowIndex, colIndex);
		}
		return contents;
	}

	Cell read(int rowIndex, int colIndex) throws IOException, ExcelException;

	@Override
	default void doRead(Exporter exporter) throws IOException {
		TypeDescriptor typeDescriptor = TypeDescriptor.array(Value.class);
		try {
			for (int row = getNumberOfRows(), r = 0; r < row; r++) {
				Cell[] values = read(r);
				exporter.doWrite(values, typeDescriptor);
			}
		} finally {
			exporter.flush();
		}
	}
}
