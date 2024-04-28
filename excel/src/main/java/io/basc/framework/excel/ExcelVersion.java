package io.basc.framework.excel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExcelVersion implements ExcelMetadata {
	XLS(".xls", 255, 65535, 256), XLSX(".xlsx", 255, 1048576, 16384);

	private final String fileExtension;
	private final int maxSheets;
	private final int maxRows;
	private final int maxColumns;

	public static ExcelVersion forFileExtension(String fileName) {
		for (ExcelVersion excelVersion : values()) {
			if (fileName.endsWith(excelVersion.fileExtension)) {
				return excelVersion;
			}
		}
		return null;
	}
}
