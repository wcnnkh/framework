package io.basc.framework.microsoft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;

public interface ExcelOperations extends ExcelReader {
	Excel create(InputStream inputStream) throws IOException, ExcelException;

	WritableExcel create(OutputStream outputStream) throws IOException, ExcelException;

	WritableExcel create(OutputStream outputStream, ExcelVersion excelVersion) throws IOException, ExcelException;

	Excel create(File file) throws IOException, ExcelException;

	WritableExcel createWritableExcel(File file) throws IOException, ExcelException;

	default ExcelExport createExport(OutputStream outputStream) throws IOException, ExcelException {
		Assert.requiredArgument(outputStream != null, "outputStream");
		return createExport(outputStream, ExcelVersion.XLS);
	}

	default ExcelExport createExport(OutputStream outputStream, @Nullable ExcelVersion excelVersion)
			throws IOException, ExcelException {
		ExcelVersion excelVersionTouse = excelVersion == null ? ExcelVersion.XLS : excelVersion;
		WritableExcel writableExcel = create(outputStream, excelVersionTouse);
		return new DefaultExcelExport(writableExcel, excelVersionTouse, 0, 0);
	}

	default ExcelExport createExport(File file) throws IOException, ExcelException {
		WritableExcel writableExcel = createWritableExcel(file);
		ExcelVersion excelVersion = ExcelVersion.forFileName(file.getName());
		if (excelVersion == null) {
			excelVersion = ExcelVersion.XLS;
		}
		return new DefaultExcelExport(writableExcel, excelVersion, 0, 0);
	}
}
