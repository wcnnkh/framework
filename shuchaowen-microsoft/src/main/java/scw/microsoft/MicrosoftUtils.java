package scw.microsoft;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scw.core.instance.InstanceUtils;
import scw.core.utils.ArrayUtils;
import scw.io.IOUtils;
import scw.io.Resource;
import scw.net.InetUtils;
import scw.net.message.OutputMessage;

public final class MicrosoftUtils {
	private MicrosoftUtils() {
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

	public static ExcelExport createExcelExport(OutputStream outputStream) throws ExcelException, IOException {
		WritableExcel writableExcel = getExcelOperations().create(outputStream);
		return new ExcelExport(writableExcel, 0, 0);
	}

	public static ExcelExport createExcelExport(OutputMessage outputMessage, String fileName) throws IOException {
		InetUtils.writeFileMessageHeaders(outputMessage, fileName);
		WritableExcel writableExcel = getExcelOperations().create(outputMessage.getBody());
		return ExcelExport.create(writableExcel, fileName, 0, 0);
	}
}
