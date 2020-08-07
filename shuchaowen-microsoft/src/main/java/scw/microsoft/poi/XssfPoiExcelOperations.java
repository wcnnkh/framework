package scw.microsoft.poi;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import scw.core.annotation.UseJavaVersion;
import scw.microsoft.ExcelException;
import scw.microsoft.WritableExcel;

@UseJavaVersion(8)
public class XssfPoiExcelOperations extends PoiExcelOperations {

	static {
		SXSSFWorkbook.class.getName();
	}

	public WritableExcel create(OutputStream outputStream) throws IOException, ExcelException {
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		return new PoiExcel(workbook, outputStream);
	}
}