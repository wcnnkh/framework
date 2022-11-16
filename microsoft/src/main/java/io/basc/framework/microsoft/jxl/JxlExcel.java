package io.basc.framework.microsoft.jxl;

import java.io.IOException;

import io.basc.framework.microsoft.Excel;
import io.basc.framework.microsoft.Sheet;
import io.basc.framework.util.Cursor;
import jxl.Workbook;

public class JxlExcel implements Excel {
	private final Workbook workbook;
	private final long cursorId;
	private final long count;

	public JxlExcel(Workbook workbook) {
		this(workbook, 0, -1);
	}

	private JxlExcel(Workbook workbook, long cursorId, long count) {
		this.workbook = workbook;
		this.cursorId = cursorId;
		this.count = count;
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
	public long getCount() {
		return count > 0 ? count : Excel.super.getCount();
	}

	@Override
	public Cursor<String[]> iterator() {
		Cursor<String[]> cursor = Excel.super.iterator();
		if (count > 0) {
			return cursor.limit(0, count);
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
