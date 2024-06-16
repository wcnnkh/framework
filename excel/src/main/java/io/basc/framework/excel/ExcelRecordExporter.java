package io.basc.framework.excel;

import java.io.IOException;

import io.basc.framework.mapper.io.template.AbstractRecordExporter;
import io.basc.framework.mapper.io.template.Record;
import io.basc.framework.mapper.io.template.RecordExporter;

/**
 * excel导出
 * 
 * @author wcnnkh
 *
 */
public final class ExcelRecordExporter extends AbstractRecordExporter implements RecordExporter {
	private final WritableExcel writableExcel;
	private final int maxSheets;
	private final int maxRows;
	private final int maxColumns;
	private int sheetIndex;
	private int rowIndex;

	public ExcelRecordExporter(WritableExcel writableExcel, ExcelVersion excelVersion) {
		this(writableExcel, excelVersion, 0, 0);
	}

	public ExcelRecordExporter(WritableExcel writableExcel, ExcelVersion excelVersion, int sheetIndex,
			int beginRowIndex) {
		this(writableExcel, excelVersion.getMaxSheets(), excelVersion.getMaxRows(), excelVersion.getMaxColumns(),
				sheetIndex, beginRowIndex);
	}

	public ExcelRecordExporter(WritableExcel writableExcel, int maxSheets, int maxRows, int maxColumns, int sheetIndex,
			int beginRowIndex) {
		this.writableExcel = writableExcel;
		this.maxSheets = maxSheets;
		this.maxRows = maxRows;
		this.maxColumns = maxColumns;
		this.sheetIndex = sheetIndex;
		this.rowIndex = beginRowIndex;
	}

	@Override
	public void doWriteRecord(Record record) throws IOException {
		if (record.isEmpty()) {
			return;
		}

		if (maxSheets > 0 && sheetIndex > maxSheets) {
			throw new ExcelException("max sheets is " + maxSheets);
		}

		if (maxColumns > 0 && record.getNumberOfElements() > maxColumns) {
			// 如果列的数量超过了这个sheet的定义那么用一个新的sheet来保存
			this.rowIndex = 0;
			sheetIndex++;
		}

		WritableSheet sheet = writableExcel.getSheet(sheetIndex);
		if (sheet == null) {
			sheet = writableExcel.createSheet();
		}
		sheet.doWriteRecord(record);
		rowIndex++;
		if (maxRows > 0 && this.rowIndex > maxRows) {
			this.rowIndex = 0;
			sheetIndex++;
		}

		// 最少每256行flush一次，已节省内存
		if (rowIndex % 256 == 0) {
			flush();
		}
	}

	public int getSheetIndex() {
		return sheetIndex;
	}

	public void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public int getMaxSheets() {
		return maxSheets;
	}

	public int getMaxRows() {
		return maxRows;
	}

	public int getMaxColumns() {
		return maxColumns;
	}

	@Override
	public void flush() throws IOException {
		writableExcel.flush();
	}
}
