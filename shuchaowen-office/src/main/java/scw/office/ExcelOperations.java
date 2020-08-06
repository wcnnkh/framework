package scw.office;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.beans.annotation.AopEnable;
import scw.net.message.OutputMessage;

@AopEnable(false)
public interface ExcelOperations {
	Excel create(InputStream inputStream) throws IOException, ExcelException;

	WritableExcel create(OutputStream outputStream) throws IOException, ExcelException;

	ExcelExport createExport(OutputStream outputStream) throws IOException, ExcelException;

	ExcelExport createExport(OutputStream outputStream, int sheetIndex, int beginRowIndex)
			throws IOException, ExcelException;

	ExcelExport createExport(OutputMessage outputMessage, String fileName) throws IOException, ExcelException;
}
