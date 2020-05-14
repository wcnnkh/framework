package scw.office.excel.servlet;

import java.util.Arrays;

import scw.db.DB;
import scw.mvc.Channel;
import scw.mvc.http.HttpView;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;
import scw.office.excel.jxl.export.JxlExport;
import scw.office.excel.jxl.export.SimpleExportRowImpl;
import scw.office.excel.jxl.export.SqlExportRow;
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
	public void render(Channel channel, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) throws Throwable {
		if (serverHttpResponse.getContentType() == null) {
			serverHttpResponse.setContentType("text/html;charset=" + serverHttpResponse.getCharacterEncoding());
		}

		JxlExport.sqlResultSetToExcel(fileName, titles, db, Arrays.asList(sql), serverHttpResponse, sqlExportRow);
	}
}
