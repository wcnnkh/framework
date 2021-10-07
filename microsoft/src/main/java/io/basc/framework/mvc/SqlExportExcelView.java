package io.basc.framework.mvc;

import io.basc.framework.microsoft.ExcelExport;
import io.basc.framework.mvc.view.View;
import io.basc.framework.sql.SimpleSqlExportRowMapping;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlExcelExport;
import io.basc.framework.sql.SqlOperations;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.web.MicrosoftWebUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlExportExcelView implements View {
	private Sql sql;
	private SqlOperations sqlOperations;
	private String fileName;
	private String[] titles;
	private Processor<ResultSet, String[], SQLException> sqlExportRowMapping;

	public SqlExportExcelView(SqlOperations sqlOperations, Sql sql, String fileName, String[] titles) {
		this(sqlOperations, sql, fileName, titles, new SimpleSqlExportRowMapping(titles.length));
	}

	public SqlExportExcelView(SqlOperations sqlOperations, Sql sql, String fileName, String[] titles,
			Processor<ResultSet, String[], SQLException> sqlExportRowMapping) {
		this.sqlOperations = sqlOperations;
		this.sql = sql;
		this.fileName = fileName;
		this.titles = titles;
		this.sqlExportRowMapping = sqlExportRowMapping;
	}

	public void render(HttpChannel httpChannel) throws IOException {
		ExcelExport excelExport = null;
		try {
			excelExport = MicrosoftWebUtils.createExcelExport(httpChannel.getResponse(), fileName + ".xls");
			SqlExcelExport.create(excelExport).export(titles, sqlOperations, sql, sqlExportRowMapping);
		} finally {
			excelExport.close();
		}
	}
}
