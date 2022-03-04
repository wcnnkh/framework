package io.basc.framework.microsoft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.lang.Nullable;

public interface ExcelOperations extends ExcelReader {
	Excel create(InputStream inputStream) throws IOException, ExcelException;

	WritableExcel create(OutputStream outputStream) throws IOException, ExcelException;

	WritableExcel create(OutputStream outputStream, ExcelVersion excelVersion) throws IOException, ExcelException;

	Excel create(File file) throws IOException, ExcelException;

	WritableExcel createWritableExcel(File file) throws IOException, ExcelException;

	default ExcelExport createExcelExport(OutputStream outputStream) throws IOException, ExcelException {
		return createExcelExport(outputStream, ExcelVersion.XLS);
	}

	ExcelExport createExcelExport(OutputStream outputStream, @Nullable ExcelVersion excelVersion)
			throws IOException, ExcelException;

	ExcelExport createExcelExport(File file) throws IOException, ExcelException;
}
