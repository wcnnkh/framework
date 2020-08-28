package scw.microsoft.jxl;

import java.io.IOException;

import jxl.write.WritableWorkbook;
import scw.microsoft.WritableExcel;
import scw.microsoft.WritableSheet;

public class JxlWritableExcel implements WritableExcel {
	private WritableWorkbook workbook;

	public JxlWritableExcel(WritableWorkbook workbook) {
		this.workbook = workbook;
	}

	public void close() throws IOException {
		workbook.close();
	}

	public void flush() throws IOException {
		workbook.write();
	}

	public scw.microsoft.WritableSheet getSheet(int sheetIndex) {
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
