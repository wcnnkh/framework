package io.basc.framework.mapper.io.table;

import java.io.IOException;

import io.basc.framework.core.execution.Parameter;
import io.basc.framework.util.Indexed;

public interface TableExporter extends TableImporter, RowExporter {

	default void doWriteRow(Row row) throws IOException {
		for (Indexed<Parameter> indexed : row.getOrderedElements().index()) {
			int index = indexed.getElement().getPositionIndex();
			if (index == -1) {
				index = (int) indexed.getIndex();
			}

			ParameterColumn parameterColumn = new ParameterColumn(indexed.getElement(), index, row);
			doWriteColumn(parameterColumn);
		}
	}

	void doWriteColumn(Column column) throws IOException;
}
