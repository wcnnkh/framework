package io.basc.framework.microsoft;

import java.io.IOException;

import io.basc.framework.util.page.PaginationsWrapper;

public class ExcelWrapper<W extends Excel> extends PaginationsWrapper<String[], W> implements Excel {

	public ExcelWrapper(W excel) {
		super(excel);
	}

	public void close() throws IOException {
		wrappedTarget.close();
	}

	@Override
	public Sheet getSheet(int sheetIndex) {
		return wrappedTarget.getSheet(sheetIndex);
	}

	@Override
	public int getNumberOfSheets() {
		return wrappedTarget.getNumberOfSheets();
	}

	@Override
	public Excel jumpTo(Long cursorId, long count) {
		return wrappedTarget.jumpTo(cursorId, count);
	}
}
