package scw.servlet.view;

import java.io.IOException;
import java.util.Arrays;

import scw.common.exception.ShuChaoWenRuntimeException;
import scw.db.AbstractDB;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.View;
import scw.sql.Sql;
import scw.utils.excel.export.JxlExport;
import scw.utils.excel.export.SimpleExportRowImpl;
import scw.utils.excel.export.SqlExportRow;

public class JxlExcelView implements View{
	private Sql sql;
	private AbstractDB db;
	private String fileName;
	private String[] titles;
	private SqlExportRow sqlExportRow;
	
	public JxlExcelView(AbstractDB db, Sql sql, String fileName, String[] titles){
		this(db, sql, fileName, titles, new SimpleExportRowImpl(titles.length));
	}
	
	public JxlExcelView(AbstractDB db, Sql sql, String fileName, String[] titles, SqlExportRow sqlExportRow){
		this.db = db;
		this.sql = sql;
		this.fileName = fileName;
		this.titles = titles;
		this.sqlExportRow = sqlExportRow;
	}

	public void render(Request request, Response response) throws IOException {
		if(response.getContentType() == null){
			response.setContentType("text/html;charset=" + response.getCharacterEncoding());
		}
		
		try {
			JxlExport.sqlResultSetToExcel(fileName, titles, db, Arrays.asList(sql), response, sqlExportRow);
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}
}
