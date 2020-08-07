package scw.microsoft.jxl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableWorkbook;
import scw.microsoft.Excel;
import scw.microsoft.ExcelException;
import scw.microsoft.ExcelOperations;
import scw.microsoft.WritableExcel;

public class JxlExcelOperations implements ExcelOperations {

	public Excel create(InputStream inputStream) throws ExcelException {
		Workbook workbook;
		try {
			workbook = Workbook.getWorkbook(inputStream);
		} catch (IOException e) {
			throw new ExcelException(e);
		} catch (BiffException e) {
			throw new ExcelException(e);
		}
		return new JxlExcel(workbook);
	}

	public WritableExcel create(OutputStream outputStream) throws ExcelException {
		WritableWorkbook workbook;
		try {
			workbook = Workbook.createWorkbook(outputStream);
		} catch (IOException e) {
			throw new ExcelException(e);
		}
		return new JxlWritableExcel(workbook);
	}
}
