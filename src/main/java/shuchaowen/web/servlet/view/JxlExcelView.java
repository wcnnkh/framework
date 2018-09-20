package shuchaowen.web.servlet.view;

import java.io.IOException;
import java.util.Arrays;

import shuchaowen.core.db.ConnectionOrigin;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.web.servlet.View;
import shuchaowen.web.servlet.WebResponse;
import shuchaowen.web.support.jxl.export.JxlExport;
import shuchaowen.web.support.jxl.export.service.SimpleExportRow;
import shuchaowen.web.support.jxl.export.service.impl.SqlExportRowImpl;

public class JxlExcelView implements View{
	private SQL sql;
	private ConnectionOrigin db;
	private String fileName;
	private String[] titles;
	private SqlExportRowImpl sqlExportRowImpl;
	
	public JxlExcelView(ConnectionOrigin db, SQL sql, String fileName, String[] titles){
		this(db, sql, fileName, titles, new SimpleExportRow(titles.length));
	}
	
	public JxlExcelView(ConnectionOrigin db, SQL sql, String fileName, String[] titles, SqlExportRowImpl sqlExportRowImpl){
		this.db = db;
		this.sql = sql;
		this.fileName = fileName;
		this.titles = titles;
		this.sqlExportRowImpl = sqlExportRowImpl;
	}

	public void render(WebResponse response) throws IOException {
		if(response.getContentType() == null){
			response.setContentType("text/html;charset=" + response.getCharacterEncoding());
		}
		
		try {
			JxlExport.sqlResultSetToExcel(fileName, titles, db, Arrays.asList(sql), response, sqlExportRowImpl);
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}
}
