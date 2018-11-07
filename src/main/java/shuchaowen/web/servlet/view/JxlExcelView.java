package shuchaowen.web.servlet.view;

import java.io.IOException;
import java.util.Arrays;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.jxl.export.JxlExport;
import shuchaowen.jxl.export.service.SqlExportRow;
import shuchaowen.jxl.export.service.impl.SimpleExportRowImpl;
import shuchaowen.web.servlet.View;
import shuchaowen.web.servlet.WebResponse;

public class JxlExcelView implements View{
	private SQL sql;
	private AbstractDB db;
	private String fileName;
	private String[] titles;
	private SqlExportRow sqlExportRow;
	
	public JxlExcelView(AbstractDB db, SQL sql, String fileName, String[] titles){
		this(db, sql, fileName, titles, new SimpleExportRowImpl(titles.length));
	}
	
	public JxlExcelView(AbstractDB db, SQL sql, String fileName, String[] titles, SqlExportRow sqlExportRow){
		this.db = db;
		this.sql = sql;
		this.fileName = fileName;
		this.titles = titles;
		this.sqlExportRow = sqlExportRow;
	}

	public void render(WebResponse response) throws IOException {
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
