package io.basc.framework.microsoft;

public enum ExcelVersion {
	XLS(".xls", 255, 65535, 256), XLSX(".xlsx", 255, 1048576, 16384);

	private final String fileSuffixName;
	private final int maxSheets;
	private final int maxRows;
	private final int maxColumns;

	ExcelVersion(String fileSuffixName, int maxSheets, int maxRows, int maxColumns) {
		this.fileSuffixName = fileSuffixName;
		this.maxSheets = maxSheets;
		this.maxRows = maxRows;
		this.maxColumns = maxColumns;
	}

	public String getFileSuffixName() {
		return fileSuffixName;
	}

	public int getMaxSheets() {
		return maxSheets;
	}

	public int getMaxRows() {
		return maxRows;
	}

	public int getMaxColumns() {
		return maxColumns;
	}

	public static ExcelVersion forFileName(String fileName) {
		for (ExcelVersion excelVersion : values()) {
			if (fileName.endsWith(excelVersion.fileSuffixName)) {
				return excelVersion;
			}
		}
		return null;
	}
}
