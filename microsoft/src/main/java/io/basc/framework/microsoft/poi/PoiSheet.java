package io.basc.framework.microsoft.poi;

import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.Sheet;
import io.basc.framework.microsoft.WritableSheet;

import java.util.Collection;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class PoiSheet implements Sheet, WritableSheet {
	private final org.apache.poi.ss.usermodel.Sheet sheet;

	public PoiSheet(org.apache.poi.ss.usermodel.Sheet sheet) {
		this.sheet = sheet;
	}

	public org.apache.poi.ss.usermodel.Sheet getSheet() {
		return sheet;
	}

	public String getName() {
		return sheet.getSheetName();
	}

	public String[] read(int rowIndex) throws ExcelException {
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			return null;
		}

		String[] values = new String[row.getLastCellNum() - row.getFirstCellNum()];
		for (int i = row.getFirstCellNum(), index = 0; i < row.getLastCellNum(); i++, index++) {
			Cell cell = row.getCell(i);
			if (cell == null) {
				continue;
			}

			values[index] = cell.getStringCellValue();
		}
		return values;
	}

	public String read(int rowIndex, int colIndex) throws ExcelException {
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			return null;
		}

		Cell cell = row.getCell(colIndex);
		if (cell == null) {
			return null;
		}

		return cell.getStringCellValue();
	}

	public int getRows() {
		return sheet.getPhysicalNumberOfRows();
	}

	public void write(int rowIndex, Collection<String> contents) throws ExcelException {
		Iterator<String> iterator = contents.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			write(rowIndex, index++, iterator.next());
		}
	}

	public void write(int rowIndex, int colIndex, String content) throws ExcelException {
		Row row = getSheet().getRow(rowIndex);
		if (row == null) {
			row = getSheet().createRow(rowIndex);
		}

		Cell cell = row.getCell(colIndex);
		if (cell == null) {
			cell = row.createCell(colIndex);
		}
		cell.setCellValue(content);
	}
}
