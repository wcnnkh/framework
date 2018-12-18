package scw.servlet.view;

import java.io.IOException;
import java.util.List;

import scw.excel.export.JxlExport;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.View;

public class JxlListToExcelView implements View{
	private String fileName;
	private String[] titles;
	private List<Object[]> list;
	
	public JxlListToExcelView(String fileName, String[] titles, List<Object[]> list) {
		this.fileName = fileName;
		this.titles = titles;
		this.list = list;
	}
	
	public void render(Request request, Response response) throws IOException {
		if(response.getContentType() == null){
			response.setContentType("text/html;charset=" + response.getCharacterEncoding());
		}
		
		try {
			JxlExport.exportExcel(fileName, titles, list, response);
		} catch (Exception e) {
			response.sendError(500, "导出失败");
			e.printStackTrace();
		}
	}

}
