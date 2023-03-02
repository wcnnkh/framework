package io.basc.framework.microsoft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 逐行读取(内存消耗小)
 * 
 * @author wcnnkh
 *
 */
public interface ExcelReader {

	void read(InputStream inputStream, Consumer<ExcelRow> consumer) throws IOException, ExcelException;

	void read(File file, Consumer<ExcelRow> consumer) throws IOException, ExcelException;

	Stream<ExcelRow> read(File source) throws IOException, ExcelException;

	Stream<ExcelRow> read(InputStream source) throws IOException, ExcelException;
}
