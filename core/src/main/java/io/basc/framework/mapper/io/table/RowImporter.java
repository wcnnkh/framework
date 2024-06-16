package io.basc.framework.mapper.io.table;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.io.Exporter;
import io.basc.framework.mapper.io.template.RecordImporter;

public interface RowImporter extends RecordImporter {

	/**
	 * 获取行数
	 * 
	 * @return
	 */
	int getNumberOfRows();

	Row readRow(int rowIndex) throws IOException;

	@Override
	default void doRead(Exporter exporter) throws IOException {
		try {
			for (int maxRowIndex = getNumberOfRows(), r = 0; r < maxRowIndex; r++) {
				Row row = readRow(r);
				exporter.doWrite(row, TypeDescriptor.forObject(row));
			}
		} finally {
			exporter.flush();
		}
	}
}
