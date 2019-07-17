package scw.utils.excel.jxl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import scw.core.io.IOUtils;
import scw.core.utils.ConfigUtils;
import scw.utils.excel.ReadExcelSupport;
import scw.utils.excel.RowCallback;

public class JxlReadExcelSupport implements ReadExcelSupport {

	public void read(String excel, RowCallback callback) {
		File file = ConfigUtils.getFile(excel);
		if (!file.exists()) {
			return;
		}

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			read(fis, callback);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.close(fis);
		}
	}

	protected Workbook getWrokBook(InputStream inputStream) throws BiffException, IOException {
		return Workbook.getWorkbook(inputStream);
	}

	public void read(InputStream inputStream, RowCallback callback) {
		Workbook workbook = null;
		try {
			workbook = getWrokBook(inputStream);
			Sheet[] sheets = workbook.getSheets();
			for (int sheetIndex = 0; sheetIndex < sheets.length; sheetIndex++) {
				Sheet sheet = sheets[sheetIndex];
				for (int rowIndex = 0; rowIndex < sheet.getRows(); rowIndex++) {
					int columns = sheet.getColumns();
					String[] contents = new String[columns];
					for (int columnIndex = 0; columnIndex < columns; columnIndex++) {
						Cell cell = sheet.getCell(columnIndex, rowIndex);
						contents[columnIndex] = cell.getContents();
					}
					callback.call(sheetIndex, rowIndex, contents);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (workbook != null) {
				workbook.close();
			}
		}
	}
}
