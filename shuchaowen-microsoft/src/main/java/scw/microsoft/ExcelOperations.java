package scw.microsoft;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.beans.annotation.AopEnable;

@AopEnable(false)
public interface ExcelOperations {
	Excel create(InputStream inputStream) throws IOException, ExcelException;

	WritableExcel create(OutputStream outputStream) throws IOException, ExcelException;
}
