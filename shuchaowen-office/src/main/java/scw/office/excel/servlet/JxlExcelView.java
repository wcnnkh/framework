package scw.office.excel.servlet;

import java.io.IOException;
import java.util.Arrays;

import scw.db.DB;
import scw.mvc.HttpChannel;
import scw.mvc.view.View;
import scw.office.excel.jxl.export.JxlExport;
import scw.office.excel.jxl.export.SimpleExportRowImpl;
import scw.office.excel.jxl.export.SqlExportRow;
import scw.sql.Sql;

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

	public void render(HttpChannel httpChannel) throws IOException {
		if (httpChannel.getResponse().getContentType() == null) {
			httpChannel.getResponse()
					.setContentType("text/html;charset=" + httpChannel.getResponse().getCharacterEncoding());
		}

		JxlExport.sqlResultSetToExcel(fileName, titles, db, Arrays.asList(sql), httpChannel.getResponse(),
				sqlExportRow);
	}
}
