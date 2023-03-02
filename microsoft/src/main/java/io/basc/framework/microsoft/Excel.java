package io.basc.framework.microsoft;

import java.io.Closeable;

import io.basc.framework.util.Cursor;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.page.Paginations;

public interface Excel extends Closeable, Paginations<String[]> {

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

	@Override
	Excel jumpTo(Long cursorId, long count);

	@Override
	default long getCount() {
		return getSheet(getCursorId().intValue()).getRows();
	}

	@Override
	default long getTotal() {
		return pages().mapToLong((e) -> getSheet(e.getCursorId().intValue()).getRows()).sum();
	}

	@Override
	default long getPages() {
		return getNumberOfSheets();
	}

	default Sheet[] getSheets() {
		Sheet[] sheets = new Sheet[getNumberOfSheets()];
		for (int i = 0; i < sheets.length; i++) {
			sheets[i] = getSheet(i);
		}
		return sheets;
	}

	default Cursor<String[]> iterator() {
		return getSheet(getCursorId().intValue()).iterator();
	}
}
