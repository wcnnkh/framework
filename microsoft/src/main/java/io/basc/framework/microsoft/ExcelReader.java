package io.basc.framework.microsoft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import javax.ws.rs.NotSupportedException;

import io.basc.framework.io.Resource;
import io.basc.framework.util.Assert;

public interface ExcelReader {
	/**
	 * 逐行读取(内存消耗小)
	 * 
	 * @param inputStream
	 * @param rowCallback
	 * @throws IOException
	 * @throws ExcelException
	 */
	void read(InputStream inputStream, RowCallback rowCallback) throws IOException, ExcelException;

	void read(File file, RowCallback rowCallback) throws IOException, ExcelException;

	/**
	 * @param source      InputStream or File or Resource ...
	 * @param rowCallback
	 * @throws IOException
	 * @throws ExcelException
	 */
	default void read(Object source, RowCallback rowCallback) throws IOException, ExcelException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(rowCallback != null, "rowCallback");
		if (source instanceof InputStream) {
			read((InputStream) source, rowCallback);
		} else if (source instanceof File) {
			read((File) source, rowCallback);
		} else if (source instanceof Resource) {
			((Resource) source).consume((input) -> read(input, rowCallback));
		} else {
			throw new NotSupportedException(source.getClass().getName());
		}
	}

	/**
	 * @param source InputStream or File or Resource ...
	 * @return
	 * @throws IOException
	 * @throws ExcelException
	 */
	Stream<String[]> read(Object source) throws IOException, ExcelException;
}
