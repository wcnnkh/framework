package scw.integration.excel.servlet;

import java.util.Arrays;

import scw.db.DB;
import scw.integration.excel.jxl.export.JxlExport;
import scw.integration.excel.jxl.export.SimpleExportRowImpl;
import scw.integration.excel.jxl.export.SqlExportRow;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.http.HttpView;
import scw.sql.Sql;

public class JxlExcelView extends HttpView {
	private Sql sql;
	private DB db;
	private String fileName;
	private String[] titles;
	private SqlExportRow sqlExportRow;

	public JxlExcelView(DB db, Sql sql, String fileName, String[] titles) {
		this(db, sql, fileName, titles, new SimpleExportRowImpl(titles.length));
	}

	public JxlExcelView(DB db, Sql sql, String fileName, String[] titles, SqlExportRow sqlExportRow) {
		this.db = db;
		this.sql = sql;
		this.fileName = fileName;
		this.titles = titles;
		this.sqlExportRow = sqlExportRow;
	}

	@Override
	public void render(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse) throws Throwable {
		if (httpResponse.getContentType() == null) {
			httpResponse.setContentType("text/html;charset=" + httpResponse.getCharacterEncoding());
		}

		JxlExport.sqlResultSetToExcel(fileName, titles, db, Arrays.asList(sql), httpResponse, sqlExportRow);
	}
}
