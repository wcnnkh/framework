package scw.microsoft.jxl;

import java.io.IOException;
import java.io.OutputStream;

import jxl.Workbook;
import jxl.write.WritableWorkbook;
import scw.microsoft.WritableExcel;
import scw.microsoft.WritableSheet;

public class JxlWritableExcel implements WritableExcel {
	private WritableWorkbook workbook;

	public JxlWritableExcel(OutputStream outputStream) throws IOException {
		this.workbook = Workbook.createWorkbook(outputStream);
	}

	public void close() throws IOException {
		workbook.close();
	}

	public void flush() throws IOException {
		workbook.write();
	}

	public scw.microsoft.WritableSheet[] getSheets() {
		jxl.write.WritableSheet[] sheets = workbook.getSheets();
		if (sheets == null || sheets.length == 0) {
			return new WritableSheet[0];
		}

		WritableSheet[] sheets2 = new WritableSheet[sheets.length];
		for (int i = 0; i < sheets.length; i++) {
			sheets2[i] = new JxlWritableSheet(sheets[i]);
		}
		return sheets2;
	}

	public scw.microsoft.WritableSheet getSheet(int sheetIndex) {
		if(sheetIndex >= workbook.getNumberOfSheets()){
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

	public WritableSheet createSheet(String sheetName, int sheetIndex) {
		jxl.write.WritableSheet sheet = workbook.createSheet(sheetName, sheetIndex);
		if (sheet == null) {
			return null;
		}
		return new JxlWritableSheet(sheet);
	}
}
