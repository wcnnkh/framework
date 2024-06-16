package io.basc.framework.mapper.io.table;

import java.io.IOException;

import io.basc.framework.mapper.io.template.Record;
import io.basc.framework.mapper.io.template.RecordExporter;

public interface RowExporter extends RowImporter, RecordExporter {

	@Override
	default void doWriteRecord(Record row) throws IOException {
		RecordRow recordRow = new RecordRow(row);
		recordRow.setPositionIndex(getNumberOfRows() + 1);
		doWriteRow(recordRow);
	}

	void doWriteRow(Row row) throws IOException;
}
