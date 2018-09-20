package shuchaowen.web.servlet.view;

import java.io.IOException;
import java.util.List;

import shuchaowen.web.servlet.View;
import shuchaowen.web.servlet.WebResponse;
import shuchaowen.web.support.jxl.export.JxlExport;

public class JxlListToExcelView implements View{
	private String fileName;
	private String[] titles;
	private List<Object[]> list;
	
	public JxlListToExcelView(String fileName, String[] titles, List<Object[]> list) {
		this.fileName = fileName;
		this.titles = titles;
		this.list = list;
	}
	
	public void render(WebResponse response) throws IOException {
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
