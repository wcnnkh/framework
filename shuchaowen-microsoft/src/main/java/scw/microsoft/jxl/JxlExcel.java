package scw.microsoft.jxl;

import java.io.IOException;
import java.io.InputStream;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import scw.microsoft.Excel;
import scw.microsoft.Sheet;

public class JxlExcel implements Excel {
	private final Workbook workbook;

	public JxlExcel(InputStream inputStream) throws IOException, BiffException {
		workbook = Workbook.getWorkbook(inputStream);
	}

	public void close() throws IOException {
		workbook.close();
	}

	public Sheet getSheet(String sheetName) {
		jxl.Sheet sheet = workbook.getSheet(sheetName);
		if (sheet == null) {
			return null;
		}
		return new JxlSheet(sheet);
	}

	public Sheet[] getSheets() {
		jxl.Sheet[] sheets = workbook.getSheets();
		if (sheets == null || sheets.length == 0) {
			return new Sheet[0];
		}

		Sheet[] sheets2 = new Sheet[sheets.length];
		for (int i = 0; i < sheets.length; i++) {
			sheets2[i] = new JxlSheet(sheets[i]);
		}
		return sheets2;
	}

	public Sheet getSheet(int sheetIndex) {
		if(sheetIndex >= workbook.getNumberOfSheets()){
			return null;
		}
		
		jxl.Sheet sheet = workbook.getSheet(sheetIndex);
		if (sheet == null) {
			return null;
		}
		return new JxlSheet(sheet);
	}
}
