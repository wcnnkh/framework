package io.basc.framework.web;

import io.basc.framework.http.HttpOutputMessage;
import io.basc.framework.http.HttpUtils;
import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.ExcelExport;
import io.basc.framework.microsoft.ExcelVersion;
import io.basc.framework.microsoft.MicrosoftUtils;
import io.basc.framework.util.ArrayUtils;

import java.io.IOException;
import java.util.List;

public class MicrosoftWebUtils {
	public static void exportExcel(HttpOutputMessage outputMessage,
			String fileName, String[] titles, List<String[]> list)
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

	public static ExcelExport createExcelExport(
			HttpOutputMessage outputMessage, String fileName)
			throws IOException, ExcelException {
		HttpUtils.writeFileMessageHeaders(outputMessage, fileName);
		ExcelVersion excelVersion = ExcelVersion.forFileName(fileName);
		if (excelVersion == null) {
			excelVersion = ExcelVersion.XLS;
		}
		return MicrosoftUtils.getExcelOperations().createExcelExport(
				outputMessage.getOutputStream(), excelVersion);
	}
}
