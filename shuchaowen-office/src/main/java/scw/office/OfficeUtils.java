package scw.office;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scw.core.instance.InstanceUtils;
import scw.core.utils.ArrayUtils;
import scw.io.IOUtils;
import scw.io.Resource;
import scw.net.message.OutputMessage;

public final class OfficeUtils {
	private OfficeUtils() {
	};

	private static final ExcelOperations EXCEL_OPERATIONS = InstanceUtils.loadService(ExcelOperations.class,
			"scw.office.jxl.JxlExcelOperations");

	public static ExcelOperations getExcelOperations() {
		return EXCEL_OPERATIONS;
	}

	/**
	 * 只会加载第一个sheet
	 * 
	 * @param resource
	 * @return
	 * @throws ExcelException
	 * @throws IOException
	 */
	public static List<String[]> loadingExcel(Resource resource) throws ExcelException, IOException {
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
	public static List<String[]> loadingExcel(InputStream inputStream) throws ExcelException, IOException {
		if (inputStream == null) {
			return Collections.emptyList();
		}

		List<String[]> list = new ArrayList<String[]>();
		Excel excel = null;
		try {
			excel = getExcelOperations().create(inputStream);
			Sheet sheet = excel.getSheet(0);
			if (sheet != null) {
				for (int i = 0; i < sheet.getRows(); i++) {
					list.add(sheet.read(i));
				}
			}
		} finally {
			if (excel != null) {
				excel.close();
			}
		}
		return list;
	}

	public static void exportExcel(OutputMessage outputMessage, String fileName, String[] titles, List<String[]> list)
			throws ExcelException, IOException {
		ExcelExport excelExport = null;
		try {
			excelExport = getExcelOperations().createExport(outputMessage, fileName);
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
}
