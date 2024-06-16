package io.basc.framework.jxl;

import java.io.IOException;

import io.basc.framework.excel.Excel;
import io.basc.framework.excel.Sheet;
import io.basc.framework.mapper.io.template.AbstractRecordImporter;
import io.basc.framework.util.Assert;
import io.basc.framework.util.function.CloseableRegistry;
import jxl.Workbook;
import lombok.Getter;

@Getter
public class JxlExcel extends AbstractRecordImporter implements Excel {
	private final Workbook workbook;
	private final CloseableRegistry<IOException> closeable = new CloseableRegistry<>();

	public JxlExcel(Workbook workbook) {
		Assert.requiredArgument(workbook != null, "workbook");
		this.workbook = workbook;
	}

	public void close() throws IOException {
		try {
			closeable.close();
		} finally {
			workbook.close();
		}
	}

	public Sheet getSheet(int sheetIndex) {
		if (sheetIndex >= workbook.getNumberOfSheets()) {
			return null;
		}

		jxl.Sheet sheet = workbook.getSheet(sheetIndex);
		if (sheet == null) {
			return null;
		}
		return new JxlSheet(sheet, sheetIndex);
	}

	public int getNumberOfSheets() {
		return workbook.getNumberOfSheets();
	}
}
