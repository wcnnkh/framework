package io.basc.framework.microsoft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.Resource;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.microsoft.mapper.ExcelMappingExport;
import io.basc.framework.microsoft.mapper.ExcelMappingReader;
import io.basc.framework.util.Assert;
import io.basc.framework.util.stream.Cursor;

public final class MicrosoftUtils {
	private static Logger logger = LoggerFactory.getLogger(MicrosoftUtils.class);

	private MicrosoftUtils() {
	};

	private static final ExcelOperations EXCEL_OPERATIONS = Sys.env.getServiceLoader(ExcelOperations.class,
			"io.basc.framework.microsoft.poi.PoiExcelOperations", "io.basc.framework.microsoft.jxl.JxlExcelOperations")
			.first();

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

	public static ExcelMappingExport export(Object target) throws ExcelException, IOException {
		return export(target, null);
	}

	public static ExcelMappingExport export(Object target, @Nullable ExcelVersion version)
			throws ExcelException, IOException {
		Assert.requiredArgument(target != null, "target");
		ExcelExport export;
		if (target instanceof OutputStream) {
			export = getExcelOperations().createExcelExport((OutputStream) target, version);
		} else if (target instanceof File) {
			export = getExcelOperations().createExcelExport((File) target);
		} else {
			throw new NotSupportedException(target.toString());
		}
		return new ExcelMappingExport(export);
	}

	public static Cursor<String[]> read(Object source) throws IOException, ExcelException {
		ExcelMappingReader reader = new ExcelMappingReader(getExcelOperations());
		return reader.read(source);
	}

	public static <T> Cursor<T> read(Object source, Class<? extends T> targetType) throws ExcelException, IOException {
		return read(source, TypeDescriptor.valueOf(targetType));
	}

	public static <T> Cursor<T> read(Object source, TypeDescriptor targetType) throws ExcelException, IOException {
		ExcelMappingReader reader = new ExcelMappingReader(getExcelOperations());
		return reader.read(source, targetType);
	}
}
