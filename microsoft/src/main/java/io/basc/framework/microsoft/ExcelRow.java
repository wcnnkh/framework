package io.basc.framework.microsoft;

import lombok.Data;

@Data
public class ExcelRow {
	private final int sheetIndex;
	private final String sheetName;
	private final long rowIndex;
	private final String[] values;

	public ExcelRow(int sheetIndex, String sheetName, long rowIndex, String[] values) {
		this.sheetIndex = sheetIndex;
		this.sheetName = sheetName;
		this.rowIndex = rowIndex;
		this.values = values;
	}

	public int getSheetIndex() {
		return sheetIndex;
	}

	public String getSheetName() {
		return sheetName;
	}

	public long getRowIndex() {
		return rowIndex;
	}

	public String[] getValues() {
		return values;
	}
}
