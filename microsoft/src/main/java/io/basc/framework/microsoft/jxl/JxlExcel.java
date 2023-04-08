package io.basc.framework.microsoft.jxl;

import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.microsoft.Excel;
import io.basc.framework.microsoft.Sheet;
import io.basc.framework.util.Streams;
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
		return limit > 0 ? limit : count();
	}

	@Override
	public Iterator<String[]> iterator() {
		Iterator<String[]> cursor = Excel.super.iterator();
		if (limit > 0) {
			return Streams.stream(cursor).limit(limit).iterator();
		}
		return cursor;
	}

	@Override
	public Stream<String[]> stream() {
		Stream<String[]> cursor = Excel.super.stream();
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
