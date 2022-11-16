package io.basc.framework.microsoft.poi;

import java.util.Collection;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.Sheet;
import io.basc.framework.microsoft.WritableSheet;

public class PoiSheet implements Sheet, WritableSheet {
	private static Logger logger = LoggerFactory.getLogger(PoiSheet.class);
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

			Object value = null;
			switch (cell.getCellType()) {
			case BLANK:
				value = "";
			case BOOLEAN:
				value = cell.getBooleanCellValue();
				break;
			case NUMERIC:
				value = cell.getNumericCellValue();
				break;
			case STRING:
				value = cell.getStringCellValue();
				break;
			default:
				logger.warn("Unable to read this cell rowIndex[{}] colIndex[{}] cellType[{}]", rowIndex, i,
						cell.getCellType());
				break;
			}
			values[index] = value == null ? null : value.toString();
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

	@Override
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
