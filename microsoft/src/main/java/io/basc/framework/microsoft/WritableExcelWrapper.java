package io.basc.framework.microsoft;

import java.io.IOException;

public class WritableExcelWrapper<W extends WritableExcel> extends ExcelWrapper<W> implements WritableExcel {

	public WritableExcelWrapper(W writableExcel) {
		super(writableExcel);
	}

	public WritableSheet getSheet(int sheetIndex) {
		return wrappedTarget.getSheet(sheetIndex);
	}

	public WritableSheet getSheet(String sheetName) {
		return wrappedTarget.getSheet(sheetName);
	}

	public WritableSheet createSheet(String sheetName) {
		return wrappedTarget.createSheet(sheetName);
	}

	public void flush() throws IOException {
		wrappedTarget.flush();
	}

	public WritableSheet createSheet() {
		return wrappedTarget.createSheet();
	}

	public void removeSheet(int sheetIndex) {
		wrappedTarget.removeSheet(sheetIndex);
	}
}
