package io.basc.framework.microsoft;

import java.io.IOException;

public class ExcelWrapper implements Excel {
	private final Excel excel;

	public ExcelWrapper(Excel excel) {
		this.excel = excel;
	}

	public Excel getExcel() {
		return excel;
	}

	public void close() throws IOException {
		excel.close();
	}

	public Sheet getSheet(int sheetIndex) {
		return excel.getSheet(sheetIndex);
	}

	public Sheet getSheet(String sheetName) {
		return excel.getSheet(sheetName);
	}

	public int getNumberOfSheets() {
		return excel.getNumberOfSheets();
	}

	@Override
	public Excel jumpTo(Long cursorId, long count) {
		return excel.jumpTo(cursorId, count);
	}

	@Override
	public long getTotal() {
		return excel.getTotal();
	}

	@Override
	public long getCount() {
		return excel.getCount();
	}

	@Override
	public Long getCursorId() {
		return excel.getCursorId();
	}
}
