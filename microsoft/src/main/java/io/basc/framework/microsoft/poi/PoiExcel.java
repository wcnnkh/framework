package io.basc.framework.microsoft.poi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Workbook;

import io.basc.framework.microsoft.Excel;
import io.basc.framework.microsoft.WritableExcel;
import io.basc.framework.microsoft.WritableSheet;
import io.basc.framework.util.Streams;

public class PoiExcel implements WritableExcel {
	private final Workbook workbook;
	private final OutputStream outputStream;
	private final boolean closeStream;
	private final long cursorId;
	private final long limit;

	public PoiExcel(Workbook workbook) {
		this(workbook, null, false);
	}

	PoiExcel(Workbook workbook, OutputStream outputStream, boolean closeStream) {
		this(workbook, outputStream, closeStream, 0, -1);
	}

	private PoiExcel(Workbook workbook, OutputStream outputStream, boolean closeStream, long cursorId, long limit) {
		this.workbook = workbook;
		this.outputStream = outputStream;
		this.closeStream = closeStream;
		this.cursorId = cursorId;
		this.limit = limit;
	}

	public void close() throws IOException {
		if (outputStream != null) {
			try {
				workbook.write(outputStream);
				outputStream.flush();
			} finally {
				if (closeStream) {
					outputStream.close();
				}
			}

		}
		workbook.close();
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public WritableSheet getSheet(int sheetIndex) {
		if (sheetIndex >= workbook.getNumberOfSheets()) {
			return null;
		}

		org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(sheetIndex);
		if (sheet == null) {
			return null;
		}

		return new PoiSheet(sheet);
	}

	public WritableSheet getSheet(String sheetName) {
		org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheet(sheetName);
		if (sheet == null) {
			return null;
		}

		return new PoiSheet(sheet);
	}

	public int getNumberOfSheets() {
		return workbook.getNumberOfSheets();
	}

	public void flush() throws IOException {
	}

	public WritableSheet createSheet(String sheetName) {
		org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet(sheetName);
		return new PoiSheet(sheet);
	}

	public WritableSheet createSheet() {
		org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet();
		return new PoiSheet(sheet);
	}

	public void removeSheet(int sheetIndex) {
		workbook.removeSheetAt(sheetIndex);
	}

	@Override
	public Excel jumpTo(Long cursorId, long count) {
		return new PoiExcel(workbook, outputStream, closeStream, cursorId, count);
	}

	@Override
	public Long getCursorId() {
		return cursorId;
	}

	@Override
	public long getLimit() {
		return limit > 0 ? limit : count();
	}

	@Override
	public Iterator<String[]> iterator() {
		Iterator<String[]> cursor = WritableExcel.super.iterator();
		if (limit > 0) {
			return Streams.stream(cursor).limit(limit).iterator();
		}
		return cursor;
	}

	@Override
	public Stream<String[]> stream() {
		Stream<String[]> stream = WritableExcel.super.stream();
		if (limit > 0) {
			return stream.limit(limit);
		}
		return stream;
	}
}
