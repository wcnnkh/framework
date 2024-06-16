package io.basc.framework.jxl;

import java.io.IOException;

import io.basc.framework.excel.Sheet;
import io.basc.framework.excel.SheetColumn;
import io.basc.framework.excel.SheetColumns;
import io.basc.framework.excel.SheetRow;
import io.basc.framework.mapper.io.table.TableImporter;
import io.basc.framework.mapper.io.template.AbstractRecordImporter;
import io.basc.framework.util.SimpleItem;
import jxl.Cell;

public class JxlSheet extends AbstractRecordImporter implements Sheet, TableImporter {
	private final jxl.Sheet sheet;
	private final int positionIndex;

	public JxlSheet(jxl.Sheet sheet, int positionIndex) {
		this.sheet = sheet;
		this.positionIndex = positionIndex;
	}

	public String getName() {
		return sheet.getName();
	}

	public jxl.Sheet getSheet() {
		return sheet;
	}

	@Override
	public SheetColumn readColumn(int rowIndex, int colIndex) throws IOException {
		Cell cell = sheet.getCell(colIndex, rowIndex);
		if (cell == null) {
			return null;
		}
		SimpleItem row = new SimpleItem();
		row.setPositionIndex(rowIndex);
		return new CellColumn(cell, row, this);
	}

	@Override
	public int getPositionIndex() {
		return positionIndex;
	}

	@Override
	public int getNumberOfRows() {
		return sheet.getRows();
	}

	@Override
	public int getNumberOfColumns() {
		return sheet.getColumns();
	}

	@Override
	public SheetRow readRow(int rowIndex) throws IOException {
		SheetColumn[] sheetColumns = new SheetColumn[getNumberOfColumns()];
		for (int i = 0; i < sheetColumns.length; i++) {
			sheetColumns[i] = readColumn(rowIndex, i);
		}
		return new SheetColumns(sheetColumns, this);
	}
}
