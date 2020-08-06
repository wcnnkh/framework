package scw.office;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;

public final class ExcelExport extends WritableExcelWrapper {
	private final int maxSheets;
	private final int maxRows;
	private final int maxColumns;
	private int sheetIndex;
	private int rowIndex;
	
	public ExcelExport(WritableExcel writableExcel, int sheetIndex, int beginRowIndex) {
		this(writableExcel, 255, 65535, 256, sheetIndex, beginRowIndex);
	}

	public ExcelExport(WritableExcel writableExcel, int maxSheets, int maxRows, int maxColumns, int sheetIndex,
			int beginRowIndex) {
		super(writableExcel);
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

		WritableSheet sheet = getSheet(sheetIndex);
		if (sheet == null) {
			sheet = createSheet("sheet-" + sheetIndex, sheetIndex);
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

	public static ExcelExport createXlsxExcelExport(WritableExcel writableExcel, int sheetIndex, int beginRowIndex) {
		return new ExcelExport(writableExcel, 255, 1048576, 16384, sheetIndex, beginRowIndex);
	}

	/**
	 * 会根据文件后缀名来自动选择
	 * 
	 * @param writableExcel
	 * @param fileName
	 * @param sheetIndex
	 * @param beginRowIndex
	 * @return
	 */
	public static ExcelExport create(WritableExcel writableExcel, String fileName, int sheetIndex, int beginRowIndex) {
		if (fileName.endsWith(".xlsx")) {
			return createXlsxExcelExport(writableExcel, sheetIndex, beginRowIndex);
		}
		return new ExcelExport(writableExcel, sheetIndex, beginRowIndex);
	}
}
