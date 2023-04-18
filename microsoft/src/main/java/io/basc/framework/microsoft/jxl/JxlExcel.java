package io.basc.framework.microsoft.jxl;

import java.io.IOException;

import io.basc.framework.microsoft.Excel;
import io.basc.framework.microsoft.Sheet;
import io.basc.framework.util.Assert;
import jxl.Workbook;

public class JxlExcel implements Excel {
	private final Workbook workbook;

	public JxlExcel(Workbook workbook) {
		Assert.requiredArgument(workbook != null, "workbook");
		this.workbook = workbook;
	}

	public void close() throws IOException {
		workbook.close();
	}

	public Sheet getSheet(int sheetIndex) {
		if (sheetIndex >= workbook.getNumberOfSheets()) {
			return null;
		}

		jxl.Sheet sheet = workbook.getSheet(sheetIndex);
		if (sheet == null) {
			return null;
		}
		return new JxlSheet(sheet);
	}

	public int getNumberOfSheets() {
		return workbook.getNumberOfSheets();
	}
}
