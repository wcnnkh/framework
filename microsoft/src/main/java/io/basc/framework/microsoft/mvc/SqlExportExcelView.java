package io.basc.framework.microsoft.mvc;

import io.basc.framework.db.DB;
import io.basc.framework.microsoft.ExcelExport;
import io.basc.framework.microsoft.MicrosoftUtils;
import io.basc.framework.microsoft.support.SimpleSqlExportRowMapping;
import io.basc.framework.microsoft.support.SqlExportUtils;
import io.basc.framework.mvc.HttpChannel;
import io.basc.framework.mvc.view.View;
import io.basc.framework.sql.Sql;
import io.basc.framework.util.stream.Processor;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlExportExcelView implements View {
	private Sql sql;
	private DB db;
	private String fileName;
	private String[] titles;
	private Processor<ResultSet, String[], SQLException> sqlExportRowMapping;

	public SqlExportExcelView(DB db, Sql sql, String fileName, String[] titles) {
		this(db, sql, fileName, titles, new SimpleSqlExportRowMapping(titles.length));
	}

	public SqlExportExcelView(DB db, Sql sql, String fileName, String[] titles,
			Processor<ResultSet, String[], SQLException> sqlExportRowMapping) {
		this.db = db;
		this.sql = sql;
		this.fileName = fileName;
		this.titles = titles;
		this.sqlExportRowMapping = sqlExportRowMapping;
	}

	public void render(HttpChannel httpChannel) throws IOException {
		ExcelExport excelExport = null;
		try {
			excelExport = MicrosoftUtils.createExcelExport(httpChannel.getResponse(), fileName + ".xls");
			SqlExportUtils.export(excelExport, titles, sqlExportRowMapping, db, sql);
		} finally {
			excelExport.close();
		}
	}
}
