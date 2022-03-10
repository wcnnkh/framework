package io.basc.framework.microsoft;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import io.basc.framework.env.Sys;
import io.basc.framework.http.HttpOutputMessage;
import io.basc.framework.http.HttpUtils;
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.Resource;
import io.basc.framework.lang.Constants;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.microsoft.annotation.ExcelResolver;

public final class MicrosoftUtils {
	private static Logger logger = LoggerFactory.getLogger(MicrosoftUtils.class);

	private MicrosoftUtils() {
	};

	private static final ExcelOperations EXCEL_OPERATIONS = Sys.env.getServiceLoader(ExcelOperations.class,
			"io.basc.framework.microsoft.poi.PoiExcelOperations", "io.basc.framework.microsoft.jxl.JxlExcelOperations")
			.first();

	private static final ExcelResolver EXCEL_RESOLVER = Sys.env.getServiceLoader(ExcelResolver.class)
			.first(() -> new ExcelResolver());

	static {
		if (EXCEL_OPERATIONS == null) {
			logger.error("not found excel support");
		} else {
			logger.info("using excel operations {}", EXCEL_OPERATIONS.getClass());
		}
	}

	public static ExcelOperations getExcelOperations() {
		if (EXCEL_OPERATIONS == null) {
			throw new NotSupportedException("excel operations");
		}
		return EXCEL_OPERATIONS;
	}

	public static ExcelResolver getExcelResolver() {
		if (EXCEL_RESOLVER == null) {
			throw new NotSupportedException(ExcelResolver.class.getName());
		}
		return EXCEL_RESOLVER;
	}

	/**
	 * 只会加载第一个sheet
	 * 
	 * @param resource
	 * @return
	 */
	public static List<String[]> loadingExcel(Resource resource) throws IOException, ExcelException {
		if (!resource.exists()) {
			return Collections.emptyList();
		}

		InputStream inputStream = null;
		try {
			inputStream = resource.getInputStream();
			return loadingExcel(inputStream);
		} finally {
			IOUtils.close(inputStream);
		}
	}

	/**
	 * 只会加载第一个sheet
	 * 
	 * @param inputStream
	 * @return
	 * @throws ExcelException
	 * @throws IOException
	 */
	public static List<String[]> loadingExcel(InputStream inputStream) throws IOException, ExcelException {
		if (inputStream == null) {
			return Collections.emptyList();
		}

		final List<String[]> list = new ArrayList<String[]>();
		getExcelOperations().read(inputStream, new RowCallback() {

			public void processRow(int sheetIndex, int rowIndex, String[] contents) {
				if (sheetIndex != 0) {
					return;
				}

				list.add(contents);
			}
		});
		return list;
	}

	/**
	 * @see ExcelReader#read(Object)
	 * @param        <T>
	 * @param type
	 * @param source
	 * @return
	 */
	public static <T> Stream<T> read(Class<T> type, Object source) {
		return getExcelResolver().read(getExcelOperations(), type, source);
	}

	public static <T> void export(Class<T> type, Stream<? extends T> source, Object target)
			throws ExcelException, IOException {
		export(type, source, target, null);
	}

	public static <T> void export(Stream<? extends T> source, Object target) throws ExcelException, IOException {
		export(source, target, null);
	}

	/**
	 * @see ExcelOperations#createExcelExport(Object, ExcelVersion)
	 * @param              <T>
	 * @param source
	 * @param target
	 * @param excelVersion
	 * @throws ExcelException
	 * @throws IOException
	 */
	public static <T> void export(Stream<? extends T> source, Object target, @Nullable ExcelVersion excelVersion)
			throws ExcelException, IOException {
		ExcelExport export = getExcelOperations().createExcelExport(target, excelVersion);
		try {
			getExcelResolver().export(export, source);
		} finally {
			export.close();
		}

	}

	/**
	 * @see ExcelOperations#createExcelExport(Object, ExcelVersion)
	 * @param              <T>
	 * @param type
	 * @param source
	 * @param target
	 * @param excelVersion
	 * @throws ExcelException
	 * @throws IOException
	 */
	public static <T> void export(Class<T> type, Stream<? extends T> source, Object target,
			@Nullable ExcelVersion excelVersion) throws ExcelException, IOException {
		ExcelExport export = getExcelOperations().createExcelExport(target, excelVersion);
		try {
			getExcelResolver().export(export, type, source);
		} finally {
			export.close();
		}
	}

	public static ExcelExport createExcelExport(HttpOutputMessage outputMessage, String fileName)
			throws IOException, ExcelException {
		return createExcelExport(outputMessage, fileName, Constants.UTF_8);
	}

	public static ExcelExport createExcelExport(HttpOutputMessage outputMessage, String fileName,
			@Nullable Charset charset) throws IOException, ExcelException {
		HttpUtils.writeFileMessageHeaders(outputMessage, fileName, charset);
		ExcelVersion excelVersion = ExcelVersion.forFileName(fileName);
		if (excelVersion == null) {
			excelVersion = ExcelVersion.XLS;
		}
		return MicrosoftUtils.getExcelOperations().createExcelExport(outputMessage.getOutputStream(), excelVersion);
	}
}
