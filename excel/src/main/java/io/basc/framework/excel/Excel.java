package io.basc.framework.excel;

import java.io.Closeable;
import java.io.IOException;

import io.basc.framework.mapper.transfer.Exporter;
import io.basc.framework.util.StringUtils;

public interface Excel extends Closeable, ExcelImporter {
	Sheet getSheet(int sheetIndex);

	int getNumberOfSheets();

	default Sheet getSheet(String sheetName) {
		for (int i = 0; i < getNumberOfSheets(); i++) {
			Sheet sheet = getSheet(i);
			if (sheet == null) {
				continue;
			}

			if (StringUtils.equals(sheetName, sheet.getName())) {
				return sheet;
			}
		}
		return null;
	}

	default Sheet[] getSheets() {
		Sheet[] sheets = new Sheet[getNumberOfSheets()];
		for (int i = 0; i < sheets.length; i++) {
			sheets[i] = getSheet(i);
		}
		return sheets;
	}

	@Override
	default void doRead(Exporter exporter) throws IOException {
		Sheet[] sheets = getSheets();
		for (Sheet sheet : sheets) {
			if (exporter instanceof SheetContextAware) {
				((SheetContextAware) exporter).setSheetContext(sheet);
			}
			sheet.doRead(exporter);
		}
		exporter.flush();
	}
}
