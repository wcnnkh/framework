package scw.microsoft.jxl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableWorkbook;
import scw.io.IOUtils;
import scw.lang.NotSupportedException;
import scw.microsoft.DefaultExcelExport;
import scw.microsoft.Excel;
import scw.microsoft.ExcelException;
import scw.microsoft.ExcelExport;
import scw.microsoft.ExcelOperations;
import scw.microsoft.ExcelVersion;
import scw.microsoft.RowCallback;
import scw.microsoft.WritableExcel;

public class JxlExcelOperations implements ExcelOperations {

	public Excel create(InputStream inputStream) throws IOException, ExcelException {
		Workbook workbook;
		try {
			workbook = Workbook.getWorkbook(inputStream);
		} catch (BiffException e) {
			throw new ExcelException("create workbook error", e);
		}
		return new JxlExcel(workbook);
	}

	public WritableExcel create(OutputStream outputStream) throws IOException, ExcelException {
		return create(outputStream, ExcelVersion.XLS);
	}

	public WritableExcel create(OutputStream outputStream, ExcelVersion excelVersion)
			throws IOException, ExcelException {
		if (excelVersion != null && excelVersion != ExcelVersion.XLS) {
			throw new NotSupportedException("Support only xls");
		}

		excelVersion = excelVersion == null? ExcelVersion.XLS:excelVersion;
		WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
		return new JxlWritableExcel(workbook);
	}

	public void read(InputStream inputStream, RowCallback rowCallback) throws IOException, ExcelException {
		Excel excel = create(inputStream);
		try {
			for (int i = 0; i < excel.getNumberOfSheets(); i++) {
				scw.microsoft.Sheet sheet = excel.getSheet(i);
				for (int r = 0; r < sheet.getRows(); r++) {
					rowCallback.processRow(i, r, sheet.read(r));
				}
			}
		} finally {
			excel.close();
		}
	}

	public void read(File file, RowCallback rowCallback) throws IOException, ExcelException {
		FileInputStream fileInputStream = new FileInputStream(file);
		try {
			read(fileInputStream, rowCallback);
		} finally {
			IOUtils.close(fileInputStream);
		}
	}

	public Excel create(File file) throws IOException, ExcelException {
		FileInputStream fileInputStream = new FileInputStream(file);
		return create(fileInputStream);
	}

	public WritableExcel createWritableExcel(File file) throws IOException, ExcelException {
		FileOutputStream outputStream = new FileOutputStream(file);
		return create(outputStream);
	}

	public ExcelExport createExcelExport(OutputStream outputStream) throws IOException, ExcelException {
		return new DefaultExcelExport(create(outputStream), ExcelVersion.XLS, 0, 0);
	}

	public ExcelExport createExcelExport(File file) throws IOException, ExcelException {
		ExcelVersion excelVersion = ExcelVersion.forFileName(file.getName());
		if (excelVersion != null && excelVersion != ExcelVersion.XLS) {
			throw new NotSupportedException("Support only xls");
		}
		
		excelVersion = excelVersion == null? ExcelVersion.XLS:excelVersion;
		WritableExcel writableExcel = createWritableExcel(file);
		return new DefaultExcelExport(writableExcel, excelVersion, 0, 0);
	}

	public ExcelExport createExcelExport(OutputStream outputStream, ExcelVersion excelVersion)
			throws IOException, ExcelException {
		ExcelVersion excelVersionTouse = excelVersion == null? ExcelVersion.XLS:excelVersion;
		WritableExcel writableExcel = create(outputStream, excelVersionTouse);
		return new DefaultExcelExport(writableExcel, excelVersionTouse, 0, 0);
	}
}
