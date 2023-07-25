package io.basc.framework.microsoft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.basc.framework.util.element.Elements;

/**
 * 逐行读取(内存消耗小)
 * 
 * @author wcnnkh
 *
 */
public interface ExcelReader {

	void read(InputStream inputStream, Consumer<? super ExcelRow> consumer) throws IOException, ExcelException;

	void read(File file, Consumer<? super ExcelRow> consumer) throws IOException, ExcelException;

	Elements<ExcelRow> read(File source) throws IOException, ExcelException;

	Stream<ExcelRow> read(InputStream source) throws IOException, ExcelException;
}
