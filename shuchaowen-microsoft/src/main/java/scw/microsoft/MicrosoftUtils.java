package scw.microsoft;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scw.core.instance.InstanceUtils;
import scw.core.utils.ArrayUtils;
import scw.http.HttpOutputMessage;
import scw.http.HttpUtils;
import scw.io.IOUtils;
import scw.io.Resource;
import scw.lang.NotSupportedException;
import scw.util.FormatUtils;

public final class MicrosoftUtils {
	private MicrosoftUtils() {
	};

	private static final ExcelOperations EXCEL_OPERATIONS = InstanceUtils.loadService(ExcelOperations.class,
			"scw.microsoft.poi.PoiExcelOperations", "scw.microsoft.jxl.JxlExcelOperations");

	static {
		if (EXCEL_OPERATIONS == null) {
			FormatUtils.warn(MicrosoftUtils.class, "not found excel support");
		} else {
			FormatUtils.info(MicrosoftUtils.class, "using excel operations {}", EXCEL_OPERATIONS.getClass());
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

	public static void exportExcel(HttpOutputMessage outputMessage, String fileName, String[] titles, List<String[]> list)
			throws IOException, ExcelException {
		ExcelExport excelExport = null;
		try {
			excelExport = createExcelExport(outputMessage, fileName);
			if (!ArrayUtils.isEmpty(titles)) {
				excelExport.append(titles);
			}

			for (String[] contents : list) {
				excelExport.append(contents);
			}
			excelExport.flush();
		} finally {
			if (excelExport != null) {
				excelExport.close();
			}
		}
	}

	public static ExcelExport createExcelExport(HttpOutputMessage outputMessage, String fileName)
			throws IOException, ExcelException {
		HttpUtils.writeFileMessageHeaders(outputMessage, fileName);
		ExcelVersion excelVersion = ExcelVersion.forFileName(fileName);
		if (excelVersion == null) {
			excelVersion = ExcelVersion.XLS;
		}
		return getExcelOperations().createExcelExport(outputMessage.getBody(), excelVersion);
	}
}
