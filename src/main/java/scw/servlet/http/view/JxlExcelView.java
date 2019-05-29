package scw.servlet.http.view;

import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import scw.db.DB;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.View;
import scw.sql.Sql;
import scw.utils.excel.jxl.export.JxlExport;
import scw.utils.excel.jxl.export.SimpleExportRowImpl;
import scw.utils.excel.jxl.export.SqlExportRow;

public class JxlExcelView implements View {
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

	public void render(Request request, Response response) throws Exception {
		if (response.getContentType() == null) {
			response.setContentType("text/html;charset=" + response.getCharacterEncoding());
		}

		JxlExport.sqlResultSetToExcel(fileName, titles, db, Arrays.asList(sql), (HttpServletResponse) response,
				sqlExportRow);
	}
}
