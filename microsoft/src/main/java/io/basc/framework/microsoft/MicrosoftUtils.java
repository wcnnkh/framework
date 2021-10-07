package io.basc.framework.microsoft;

import io.basc.framework.env.Sys;
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.Resource;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MicrosoftUtils {
	private static Logger logger = LoggerFactory.getLogger(MicrosoftUtils.class);
	private MicrosoftUtils() {
	};

	private static final ExcelOperations EXCEL_OPERATIONS = Sys.env.getServiceLoader(ExcelOperations.class,
			"io.basc.framework.microsoft.poi.PoiExcelOperations", "io.basc.framework.microsoft.jxl.JxlExcelOperations").first();

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
}
