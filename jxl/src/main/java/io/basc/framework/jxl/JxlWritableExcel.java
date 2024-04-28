package io.basc.framework.jxl;

import java.io.IOException;

import io.basc.framework.excel.ExcelException;
import io.basc.framework.excel.WritableExcel;
import io.basc.framework.excel.WritableSheet;
import io.basc.framework.mapper.transfer.AbstractRowExporter;
import io.basc.framework.util.Assert;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class JxlWritableExcel extends AbstractRowExporter implements WritableExcel {
	private WritableWorkbook workbook;

	public JxlWritableExcel(WritableWorkbook workbook) {
		Assert.requiredArgument(workbook != null, "workbook");
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

	public WritableSheet getSheet(int sheetIndex) {
		if (sheetIndex >= workbook.getNumberOfSheets()) {
			return null;
		}

		jxl.write.WritableSheet sheet = workbook.getSheet(sheetIndex);
		if (sheet == null) {
			return null;
		}
		return new JxlWritableSheet(sheet, sheetIndex);
	}

	public WritableSheet getSheet(String sheetName) {
		jxl.write.WritableSheet sheet = workbook.getSheet(sheetName);
		if (sheet == null) {
			return null;
		}
		return new JxlWritableSheet(sheet, -1);
	}

	public WritableSheet createSheet(String sheetName) {
		int index = getNumberOfSheets() + 1;
		jxl.write.WritableSheet sheet = workbook.createSheet(sheetName, index);
		if (sheet == null) {
			return null;
		}
		return new JxlWritableSheet(sheet, index);
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
