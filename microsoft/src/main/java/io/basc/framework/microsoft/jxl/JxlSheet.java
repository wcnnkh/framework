package io.basc.framework.microsoft.jxl;

import java.io.IOException;

import io.basc.framework.microsoft.Sheet;
import jxl.Cell;

public class JxlSheet implements io.basc.framework.microsoft.Sheet {
	private final jxl.Sheet sheet;
	private final long cursorId;
	private final long count;

	public JxlSheet(jxl.Sheet sheet) {
		this(sheet, 0, Long.MAX_VALUE);
	}

	public JxlSheet(jxl.Sheet sheet, long cursorId, long count) {
		this.sheet = sheet;
		this.cursorId = cursorId;
		this.count = count;
	}

	public String getName() {
		return sheet.getName();
	}

	public jxl.Sheet getSheet() {
		return sheet;
	}

	public String[] read(int rowIndex) throws IOException {
		String[] contents = new String[sheet.getColumns()];
		for (int colIndex = 0; colIndex < contents.length; colIndex++) {
			contents[colIndex] = read(rowIndex, colIndex);
		}
		return contents;
	}

	public String read(int rowIndex, int colIndex) throws IOException {
		Cell cell = sheet.getCell(colIndex, rowIndex);
		return cell.getContents();
	}

	@Override
	public long getTotal() {
		return sheet.getRows();
	}

	@Override
	public long getCount() {
		return count;
	}

	@Override
	public Long getCursorId() {
		return cursorId;
	}

	@Override
	public Sheet jumpTo(Long cursorId, long count) {
		return new JxlSheet(this.sheet, cursorId, count);
	}
}
