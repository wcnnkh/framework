package scw.office.mvc;

import java.io.IOException;

import scw.db.DB;
import scw.mvc.HttpChannel;
import scw.mvc.view.View;
import scw.office.ExcelExport;
import scw.office.OfficeUtils;
import scw.office.support.SimpleSqlExportRowMapping;
import scw.office.support.SqlExportRowMapping;
import scw.office.support.SqlExportUtils;
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
			excelExport = OfficeUtils.getExcelOperations().createExport(httpChannel.getResponse(), fileName);
			SqlExportUtils.export(excelExport, titles, sqlExportRowMapping, db, sql);
		} finally {
			excelExport.close();
		}
	}
}
