package scw.microsoft.poi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import scw.core.utils.ClassUtils;
import scw.microsoft.Excel;
import scw.microsoft.ExcelException;
import scw.microsoft.ExcelOperations;
import scw.microsoft.WritableExcel;

public class PoiExcelOperations implements ExcelOperations {
	private boolean xssf;

	public PoiExcelOperations() {
		this(ClassUtils.isPresent("org.apache.poi.xssf.usermodel.XSSFWorkbookFactory"));
	}

	public PoiExcelOperations(boolean xssf) {
		this.xssf = xssf;
	}

	public boolean isXssf() {
		return xssf;
	}

	public void setXssf(boolean xssf) {
		this.xssf = xssf;
	}

	public Excel create(InputStream inputStream) throws IOException, ExcelException {
		Workbook workbook = WorkbookFactory.create(inputStream);
		return new PoiExcel(workbook);
	}

	public WritableExcel create(OutputStream outputStream) throws IOException, ExcelException {
		Workbook workbook = WorkbookFactory.create(xssf);
		return new PoiExcel(workbook, outputStream);
	}

}
