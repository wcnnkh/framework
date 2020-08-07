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

	public Excel create(InputStream inputStream) throws IOException, ExcelException {
		Workbook workbook;
		try {
			workbook = Workbook.getWorkbook(inputStream);
		} catch (BiffException e) {
			throw new ExcelException(e);
		}
		return new JxlExcel(workbook);
	}

	public WritableExcel create(OutputStream outputStream) throws IOException, ExcelException {
		WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
		return new JxlWritableExcel(workbook);
	}
}
