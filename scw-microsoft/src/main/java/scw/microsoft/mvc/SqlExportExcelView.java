package scw.microsoft.mvc;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import scw.db.DB;
import scw.microsoft.ExcelExport;
import scw.microsoft.MicrosoftUtils;
import scw.microsoft.support.SimpleSqlExportRowMapping;
import scw.microsoft.support.SqlExportUtils;
import scw.mvc.HttpChannel;
import scw.mvc.view.View;
import scw.sql.Sql;
import scw.util.stream.Processor;

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
