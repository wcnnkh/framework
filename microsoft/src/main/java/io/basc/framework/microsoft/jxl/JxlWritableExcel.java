package io.basc.framework.microsoft.jxl;

import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.microsoft.Excel;
import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.WritableExcel;
import io.basc.framework.microsoft.WritableSheet;
import io.basc.framework.util.Streams;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class JxlWritableExcel implements WritableExcel {
	private WritableWorkbook workbook;
	private final long cursorId;
	private final long limit;

	public JxlWritableExcel(WritableWorkbook workbook) {
		this(workbook, 0, -1);
	}

	private JxlWritableExcel(WritableWorkbook workbook, long cursorId, long limit) {
		this.workbook = workbook;
		this.cursorId = cursorId;
		this.limit = limit;
	}

	@Override
	public long getLimit() {
		return limit > 0 ? limit : WritableExcel.super.count();
	}

	public void close() throws IOException {
		try {
			workbook.close();
		} catch (WriteException e) {
			throw new ExcelException(e);
		}
	}

	public void flush() throws IOException {
		workbook.write();
	}

	public io.basc.framework.microsoft.WritableSheet getSheet(int sheetIndex) {
		if (sheetIndex >= workbook.getNumberOfSheets()) {
			return null;
		}

		jxl.write.WritableSheet sheet = workbook.getSheet(sheetIndex);
		if (sheet == null) {
			return null;
		}
		return new JxlWritableSheet(sheet);
	}

	public WritableSheet getSheet(String sheetName) {
		jxl.write.WritableSheet sheet = workbook.getSheet(sheetName);
		if (sheet == null) {
			return null;
		}
		return new JxlWritableSheet(sheet);
	}

	public WritableSheet createSheet(String sheetName) {
		jxl.write.WritableSheet sheet = workbook.createSheet(sheetName, getNumberOfSheets() + 1);
		if (sheet == null) {
			return null;
		}
		return new JxlWritableSheet(sheet);
	}

	public int getNumberOfSheets() {
		return workbook.getNumberOfSheets();
	}

	public WritableSheet createSheet() {
		return createSheet("sheet-");
	}

	public void removeSheet(int sheetIndex) {
		workbook.removeSheet(sheetIndex);
	}

	@Override
	public Iterator<String[]> iterator() {
		Iterator<String[]> iterator = WritableExcel.super.iterator();
		if (limit > 0) {
			return Streams.stream(iterator).limit(limit).iterator();
		}
		return iterator;
	}

	@Override
	public Stream<String[]> stream() {
		Stream<String[]> iterator = WritableExcel.super.stream();
		if (limit > 0) {
			return iterator.limit(limit);
		}
		return iterator;
	}

	@Override
	public Excel jumpTo(Long cursorId, long count) {
		return new JxlWritableExcel(workbook, cursorId, count);
	}

	@Override
	public Long getCursorId() {
		return cursorId;
	}
}
