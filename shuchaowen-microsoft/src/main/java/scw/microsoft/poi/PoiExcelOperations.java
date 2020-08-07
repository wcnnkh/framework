package scw.microsoft.poi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory;
import org.apache.poi.ss.usermodel.Workbook;

import scw.core.annotation.UseJavaVersion;
import scw.microsoft.Excel;
import scw.microsoft.ExcelException;
import scw.microsoft.ExcelOperations;
import scw.microsoft.WritableExcel;

@UseJavaVersion(8)
public class PoiExcelOperations implements ExcelOperations {

	public Excel create(InputStream inputStream) throws ExcelException {
		Workbook workbook;
		try {
			workbook = HSSFWorkbookFactory.create(inputStream);
		} catch (IOException e) {
			throw new ExcelException(e);
		}
		return new PoiExcel(workbook);
	}

	public WritableExcel create(OutputStream outputStream) throws ExcelException {
		Workbook workbook = HSSFWorkbookFactory.createWorkbook();
		return new PoiExcel(workbook, outputStream);
	}

}
