package io.basc.framework.poi.ss;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import io.basc.framework.core.execution.param.Parameter;
import io.basc.framework.excel.ExcelException;
import io.basc.framework.excel.Sheet;
import io.basc.framework.excel.SheetRow;
import io.basc.framework.excel.WritableSheet;
import io.basc.framework.mapper.io.template.AbstractRecordExporter;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

public class PoiSheet extends AbstractRecordExporter implements Sheet, WritableSheet {
	private static Logger logger = LogManager.getLogger(PoiSheet.class);
	private final org.apache.poi.ss.usermodel.Sheet sheet;
	private final int positionIndex;

	public PoiSheet(org.apache.poi.ss.usermodel.Sheet sheet, int positionIndex) {
		this.sheet = sheet;
		this.positionIndex = positionIndex;
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

	@Override
	public SheetRow readRow(int rowIndex) throws IOException {
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			return null;
		}
		return new PoiRow(row, this);
	}

	@Override
	public int getNumberOfRows() {
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

	@Override
	public int getPositionIndex() {
		return positionIndex;
	}

	@Override
	public void doWriteRow(io.basc.framework.mapper.io.table.Row row) throws IOException {
		Row writeRow = getSheet().getRow(row.getPositionIndex());
		if (writeRow == null) {
			writeRow = getSheet().createRow(row.getPositionIndex());
		}

		for (Parameter parameter : row.getElements()) {
			Cell cell = writeRow.getCell(parameter.getPositionIndex());
			if (cell == null) {
				cell = writeRow.createCell(parameter.getPositionIndex());
			}
			cell.setCellValue(parameter.getAsString());
		}
	}
}
