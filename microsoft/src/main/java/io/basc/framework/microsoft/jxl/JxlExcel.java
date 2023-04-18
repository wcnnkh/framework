package io.basc.framework.microsoft.jxl;

import java.io.IOException;

import io.basc.framework.microsoft.Excel;
import io.basc.framework.microsoft.Sheet;
import io.basc.framework.util.Elements;
import jxl.Workbook;

public class JxlExcel implements Excel {
	private final Workbook workbook;
	private final long cursorId;
	private final long limit;

	public JxlExcel(Workbook workbook) {
		this(workbook, 0, -1);
	}

	private JxlExcel(Workbook workbook, long cursorId, long limit) {
		this.workbook = workbook;
		this.cursorId = cursorId;
		this.limit = limit;
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

	@Override
	public long getLimit() {
		return limit > 0 ? limit : getTotal();
	}

	@Override
	public Elements<String[]> getElements() {
		Elements<String[]> cursor = Excel.super.getElements();
		if (limit > 0) {
			return cursor.limit(limit);
		}
		return cursor;
	}

	@Override
	public Long getCursorId() {
		return cursorId;
	}

	@Override
	public Excel jumpTo(Long cursorId, long count) {
		return new JxlExcel(workbook, cursorId, count);
	}
}
