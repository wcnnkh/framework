package scw.microsoft;

import java.io.IOException;

public class WritableExcelWrapper extends ExcelWrapper implements WritableExcel {

	public WritableExcelWrapper(WritableExcel writableExcel) {
		super(writableExcel);
	}

	@Override
	public WritableExcel getExcel() {
		return (WritableExcel) super.getExcel();
	}

	public WritableSheet getSheet(int sheetIndex) {
		return getExcel().getSheet(sheetIndex);
	}

	public WritableSheet getSheet(String sheetName) {
		return getExcel().getSheet(sheetName);
	}

	public WritableSheet createSheet(String sheetName) {
		return getExcel().createSheet(sheetName);
	}

	public void flush() throws IOException {
		getExcel().flush();
	}

	public WritableSheet createSheet() {
		return getExcel().createSheet();
	}

	public void removeSheet(int sheetIndex) {
		getExcel().removeSheet(sheetIndex);
	}
}
