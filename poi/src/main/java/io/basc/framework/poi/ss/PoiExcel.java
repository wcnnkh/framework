package io.basc.framework.poi.ss;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Workbook;

import io.basc.framework.excel.WritableExcel;
import io.basc.framework.excel.WritableSheet;
import io.basc.framework.util.function.StandardCloseable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PoiExcel extends StandardCloseable<IOException, PoiExcel> implements WritableExcel {
	private final Workbook workbook;
	private OutputStream outputStream;

	public PoiExcel(Workbook workbook) {
		this.workbook = workbook;
	}

	public void close() throws IOException {
		try {
			if (outputStream != null) {
				workbook.write(outputStream);
				outputStream.flush();
			}
		} finally {
			try {
				super.close();
			} finally {
				workbook.close();
			}
		}
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public WritableSheet getSheet(int sheetIndex) {
		if (sheetIndex >= workbook.getNumberOfSheets()) {
			return null;
		}

		org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(sheetIndex);
		if (sheet == null) {
			return null;
		}

		return new PoiSheet(sheet, sheetIndex);
	}

	public int getNumberOfSheets() {
		return workbook.getNumberOfSheets();
	}

	public WritableSheet createSheet(String sheetName) {
		org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet(sheetName);
		return new PoiSheet(sheet, getNumberOfSheets() + 1);
	}

	public WritableSheet createSheet() {
		org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet();
		return new PoiSheet(sheet, getNumberOfSheets() + 1);
	}

	public void removeSheet(int sheetIndex) {
		workbook.removeSheetAt(sheetIndex);
	}
}
