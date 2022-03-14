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

	/**
	 * 
	 * @param <E>
	 * @param inputStream
	 * @param consumer
	 * @throws IOException
	 * @throws ExcelException
	 * @throws E
	 */
	void read(InputStream inputStream, Consumer<ExcelRow> consumer) throws IOException, ExcelException;

	/**
	 * 
	 * @param <E>
	 * @param file
	 * @param consumer
	 * @throws IOException
	 * @throws ExcelException
	 * @throws E
	 */
	void read(File file, Consumer<ExcelRow> consumer) throws IOException, ExcelException;

	/**
	 * 
	 * @param source
	 * @return
	 * @throws IOException
	 * @throws ExcelException
	 */
	Stream<ExcelRow> read(File source) throws IOException, ExcelException;

	/**
	 * 
	 * @param source
	 * @return
	 * @throws IOException
	 * @throws ExcelException
	 */
	Stream<ExcelRow> read(InputStream source) throws IOException, ExcelException;
}
