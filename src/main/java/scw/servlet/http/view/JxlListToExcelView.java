package scw.servlet.http.view;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.View;
import scw.utils.excel.jxl.export.JxlExport;

public class JxlListToExcelView implements View {
	private String fileName;
	private String[] titles;
	private List<Object[]> list;

	public JxlListToExcelView(String fileName, String[] titles, List<Object[]> list) {
		this.fileName = fileName;
		this.titles = titles;
		this.list = list;
	}

	public void render(Request request, Response response) throws Exception {
		if (response.getContentType() == null) {
			response.setContentType("text/html;charset=" + response.getCharacterEncoding());
		}

		JxlExport.exportExcel(fileName, titles, list, (HttpServletResponse) response);
	}

}
