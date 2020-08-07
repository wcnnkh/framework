package scw.microsoft.mvc;

import java.io.IOException;

import scw.db.DB;
import scw.microsoft.ExcelExport;
import scw.microsoft.MicrosoftUtils;
import scw.microsoft.support.SimpleSqlExportRowMapping;
import scw.microsoft.support.SqlExportRowMapping;
import scw.microsoft.support.SqlExportUtils;
import scw.mvc.HttpChannel;
import scw.mvc.view.View;
import scw.sql.Sql;

public class SqlExportExcelView implements View {
	private Sql sql;
	private DB db;
	private String fileName;
	private String[] titles;
	private SqlExportRowMapping sqlExportRowMapping;

	public SqlExportExcelView(DB db, Sql sql, String fileName, String[] titles) {
		this(db, sql, fileName, titles, new SimpleSqlExportRowMapping(titles.length));
	}

	public SqlExportExcelView(DB db, Sql sql, String fileName, String[] titles, SqlExportRowMapping sqlExportRowMapping) {
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
