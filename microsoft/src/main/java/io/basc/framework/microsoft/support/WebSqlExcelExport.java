package io.basc.framework.microsoft.support;

import java.io.IOException;

import io.basc.framework.http.HttpOutputMessage;
import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.ExcelExport;

public class WebSqlExcelExport extends SqlExcelExport {

	public WebSqlExcelExport(ExcelExport excelExport) {
		super(excelExport);
	}

	public static WebSqlExcelExport create(HttpOutputMessage outputMessage,
			String fileName) throws ExcelException, IOException {
		ExcelExport excelExport = MicrosoftWebUtils.createExcelExport(
				outputMessage, fileName);
		return new WebSqlExcelExport(excelExport);
	}
}
