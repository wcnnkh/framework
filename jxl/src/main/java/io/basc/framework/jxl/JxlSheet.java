package io.basc.framework.jxl;

import java.io.IOException;

import io.basc.framework.excel.Sheet;
import jxl.Cell;

public class JxlSheet implements Sheet {
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

	public io.basc.framework.excel.Cell read(int rowIndex, int colIndex) throws IOException {
		Cell cell = sheet.getCell(colIndex, rowIndex);
		return new JxlCell(cell, this);
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
}
