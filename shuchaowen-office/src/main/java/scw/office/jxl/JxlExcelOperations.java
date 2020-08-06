package scw.office.jxl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jxl.read.biff.BiffException;
import scw.office.Excel;
import scw.office.ExcelException;
import scw.office.ExcelOperations;
import scw.office.WritableExcel;

public class JxlExcelOperations implements ExcelOperations {

	public Excel create(InputStream inputStream) throws IOException, ExcelException {
		try {
			return new JxlExcel(inputStream);
		} catch (BiffException e) {
			throw new ExcelException(e);
		}
	}

	public WritableExcel create(OutputStream outputStream) throws IOException, ExcelException {
		return new JxlWritableExcel(outputStream);
	}
}
