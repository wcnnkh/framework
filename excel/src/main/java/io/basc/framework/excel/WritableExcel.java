package io.basc.framework.excel;

import java.io.IOException;

import io.basc.framework.convert.lang.Value;
import io.basc.framework.util.element.Elements;

public interface WritableExcel extends ExcelExporter, Excel {

	WritableSheet getSheet(int sheetIndex);

	WritableSheet getSheet(String sheetName);

	WritableSheet createSheet();

	WritableSheet createSheet(String sheetName);

	void removeSheet(int sheetIndex);

	@Override
	default void doWriteValues(Elements<? extends Value> values) throws IOException {
		WritableSheet sheet = getSheet(getNumberOfSheets());
		sheet.doWriteValues(values);
	}
}
