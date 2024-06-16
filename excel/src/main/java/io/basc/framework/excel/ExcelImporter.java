package io.basc.framework.excel;

import java.io.IOException;

import io.basc.framework.mapper.io.Exporter;
import io.basc.framework.mapper.io.Importer;
import io.basc.framework.util.StringUtils;

public interface ExcelImporter extends Importer {
	Sheet getSheet(int positionIndex);

	default Sheet[] getSheets() {
		Sheet[] sheets = new Sheet[getNumberOfSheets()];
		for (int i = 0; i < sheets.length; i++) {
			sheets[i] = getSheet(i);
		}
		return sheets;
	}

	/**
	 * sheet的数量
	 * 
	 * @return
	 */
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

	@Override
	default void doRead(Exporter exporter) throws IOException {
		Sheet[] sheets = getSheets();
		try {
			for (Sheet sheet : sheets) {
				if (exporter instanceof SheetContextAware) {
					((SheetContextAware) exporter).setSheetContext(sheet);
				}
				sheet.doRead(exporter);
			}
		} finally {
			exporter.flush();
		}
	}
}
