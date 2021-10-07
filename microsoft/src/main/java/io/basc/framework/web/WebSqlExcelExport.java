package io.basc.framework.web;

import io.basc.framework.http.HttpOutputMessage;
import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.ExcelExport;
import io.basc.framework.sql.SqlExcelExport;

import java.io.IOException;

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
