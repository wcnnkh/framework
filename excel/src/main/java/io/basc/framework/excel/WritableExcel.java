package io.basc.framework.excel;

public interface WritableExcel extends ExcelExporter, Excel {
	WritableSheet createSheet();

	WritableSheet createSheet(String sheetName);

	void removeSheet(int sheetIndex);
}
