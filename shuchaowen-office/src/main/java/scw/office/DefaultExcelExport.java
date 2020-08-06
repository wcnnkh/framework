package scw.office;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;

public class DefaultExcelExport implements ExcelExport {
	final WritableExcel writableExcel;
	private final int maxSheets;
	private final int maxRows;
	private final int maxColumns;
	private int sheetIndex;
	private int rowIndex;

	public DefaultExcelExport(WritableExcel writableExcel, int maxSheets, int maxRows, int maxColumns, int sheetIndex,
			int beginRowIndex) {
		this.writableExcel = writableExcel;
		this.maxSheets = maxSheets;
		this.maxRows = maxRows;
		this.maxColumns = maxColumns;
		this.sheetIndex = sheetIndex;
		this.rowIndex = beginRowIndex;
	}

	public void append(Collection<String> contents) throws IOException, ExcelException {
		if (CollectionUtils.isEmpty(contents)) {
			return;
		}

		if (maxSheets > 0 && sheetIndex > maxSheets) {
			throw new ExcelException("max sheets is " + maxSheets);
		}

		if (maxColumns > 0 && contents.size() > maxColumns) {
			throw new ExcelException("max rows is " + maxColumns);
		}

		WritableSheet sheet = writableExcel.getSheet(sheetIndex);
		if (sheet == null) {
			sheet = writableExcel.createSheet("sheet-" + sheetIndex, sheetIndex);
		}

		sheet.write(rowIndex, contents);
		rowIndex++;
		if (maxRows > 0 && this.rowIndex > maxRows) {
			this.rowIndex = 0;
			sheetIndex++;
		}
	}

	public void append(String... contents) throws IOException, ExcelException {
		if (ArrayUtils.isEmpty(contents)) {
			return;
		}
		append(Arrays.asList(contents));
	}

	public void flush() throws IOException {
		writableExcel.flush();
	}

	public void close() throws IOException {
		writableExcel.close();
	}

	public static DefaultExcelExport createExcelExport(WritableExcel writableExcel, int sheetIndex, int beginRowIndex)
			throws ExcelException, IOException {
		return new DefaultExcelExport(writableExcel, 255, 65535, 256, sheetIndex, beginRowIndex);
	}

	public static DefaultExcelExport createXlsxExcelExport(WritableExcel writableExcel, int sheetIndex,
			int beginRowIndex) throws ExcelException, IOException {
		return new DefaultExcelExport(writableExcel, 255, 1048576, 16384, sheetIndex, beginRowIndex);
	}
}
