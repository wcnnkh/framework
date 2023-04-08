package io.basc.framework.microsoft;

import java.io.Closeable;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import io.basc.framework.util.StringUtils;
import io.basc.framework.util.page.Page;
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
	default long count() {
		return getSheet(getCursorId().intValue()).getRows();
	}

	@Override
	default long getTotal() {
		Stream<? extends Page<Long, String[]>> stream = pages().stream();
		try {
			return stream.mapToLong((e) -> getSheet(e.getCursorId().intValue()).getRows()).sum();
		} finally {
			stream.close();
		}
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

	default Iterator<String[]> iterator() {
		return getSheet(getCursorId().intValue()).iterator();
	}

	@Override
	default Stream<String[]> stream() {
		return getSheet(getCursorId().intValue()).stream();
	}

	@Override
	default List<String[]> getList() {
		return toList();
	}
}
