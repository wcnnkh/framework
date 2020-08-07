package scw.microsoft;

import java.io.IOException;

public class WritableExcelWrapper implements WritableExcel {
	private final WritableExcel writableExcel;

	public WritableExcelWrapper(WritableExcel writableExcel) {
		this.writableExcel = writableExcel;
	}

	public void close() throws IOException {
		writableExcel.close();
	}

	public void flush() throws IOException {
		writableExcel.flush();
	}

	public WritableSheet[] getSheets() {
		return writableExcel.getSheets();
	}

	public WritableSheet getSheet(int sheetIndex) {
		return writableExcel.getSheet(sheetIndex);
	}

	public WritableSheet getSheet(String sheetName) {
		return writableExcel.getSheet(sheetName);
	}

	public WritableSheet createSheet(String sheetName, int sheetIndex) {
		return writableExcel.createSheet(sheetName, sheetIndex);
	}
}
