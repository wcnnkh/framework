package io.basc.framework.microsoft.jxl;

import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.WritableExcel;
import io.basc.framework.microsoft.WritableSheet;

import java.io.IOException;

import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class JxlWritableExcel implements WritableExcel {
	private WritableWorkbook workbook;

	public JxlWritableExcel(WritableWorkbook workbook) {
		this.workbook = workbook;
	}

	public void close() throws IOException {
		try {
			workbook.close();
		} catch (WriteException e) {
			throw new ExcelException(e);
		}
	}

	public void flush() throws IOException {
		workbook.write();
	}

	public io.basc.framework.microsoft.WritableSheet getSheet(int sheetIndex) {
		if (sheetIndex >= workbook.getNumberOfSheets()) {
			return null;
		}

		jxl.write.WritableSheet sheet = workbook.getSheet(sheetIndex);
		if (sheet == null) {
			return null;
		}
		return new JxlWritableSheet(sheet);
	}

	public WritableSheet getSheet(String sheetName) {
		jxl.write.WritableSheet sheet = workbook.getSheet(sheetName);
		if (sheet == null) {
			return null;
		}
		return new JxlWritableSheet(sheet);
	}

	public WritableSheet createSheet(String sheetName) {
		jxl.write.WritableSheet sheet = workbook.createSheet(sheetName, getNumberOfSheets() + 1);
		if (sheet == null) {
			return null;
		}
		return new JxlWritableSheet(sheet);
	}

	public int getNumberOfSheets() {
		return workbook.getNumberOfSheets();
	}

	public WritableSheet createSheet() {
		return createSheet("sheet-");
	}

	public void removeSheet(int sheetIndex) {
		workbook.removeSheet(sheetIndex);
	}
}
