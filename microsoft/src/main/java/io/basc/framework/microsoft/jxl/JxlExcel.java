package io.basc.framework.microsoft.jxl;

import java.io.IOException;
import java.util.Arrays;

import io.basc.framework.microsoft.Excel;
import io.basc.framework.microsoft.Sheet;
import io.basc.framework.util.stream.Cursor;
import io.basc.framework.util.stream.StreamProcessorSupport;
import jxl.Workbook;

public class JxlExcel implements Excel {
	private final Workbook workbook;

	public JxlExcel(Workbook workbook) {
		this.workbook = workbook;
	}

	public void close() throws IOException {
		workbook.close();
	}
	
	@Override
	public Cursor<? extends Sheet> stream() {
		jxl.Sheet[] sheets = workbook.getSheets();
		if (sheets == null || sheets.length == 0) {
			return StreamProcessorSupport.emptyCursor();
		}
		return new Cursor<Sheet>(Arrays.asList(sheets).stream().map((s) -> new JxlSheet(s)));
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
