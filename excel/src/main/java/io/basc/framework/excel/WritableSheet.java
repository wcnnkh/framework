package io.basc.framework.excel;

import java.io.IOException;
import java.util.Iterator;

import io.basc.framework.util.element.Elements;
import io.basc.framework.value.Value;

public interface WritableSheet extends ExcelExporter, Sheet {

	default void doWriteRow(int rowIndex, Iterable<?> row) throws IOException, ExcelException {
		Iterator<?> iterator = row.iterator();
		int colIndex = 0;
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			Value value;
			if (obj instanceof Value) {
				value = (Value) obj;
			} else {
				value = Value.of(obj);
			}
			doWriteColumn(rowIndex, colIndex++, value);
		}
	}

	@Override
	default void doWriteValues(Elements<? extends Value> values) throws IOException {
		doWriteRow(getNumberOfRows() + 1, values);
	}

	void doWriteColumn(int rowIndex, int colIndex, Value column) throws IOException, ExcelException;
}
