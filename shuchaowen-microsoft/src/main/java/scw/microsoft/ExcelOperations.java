package scw.microsoft;

import java.io.InputStream;
import java.io.OutputStream;

import scw.beans.annotation.AopEnable;

@AopEnable(false)
public interface ExcelOperations {
	Excel create(InputStream inputStream) throws ExcelException;

	WritableExcel create(OutputStream outputStream) throws ExcelException;
}
