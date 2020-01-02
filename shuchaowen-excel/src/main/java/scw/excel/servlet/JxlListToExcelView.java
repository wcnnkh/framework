package scw.excel.servlet;

import java.util.List;

import scw.excel.jxl.export.JxlExport;
import scw.mvc.Request;
import scw.mvc.Response;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.http.HttpView;

public class JxlListToExcelView extends HttpView {
	private String fileName;
	private String[] titles;
	private List<Object[]> list;

	public JxlListToExcelView(String fileName, String[] titles, List<Object[]> list) {
		this.fileName = fileName;
		this.titles = titles;
		this.list = list;
	}

	public void render(Request request, Response response) throws Exception {

	}

	@Override
	public void render(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse) throws Throwable {
		if (httpResponse.getContentType() == null) {
			httpResponse.setContentType("text/html;charset=" + httpResponse.getCharacterEncoding());
		}

		JxlExport.exportExcel(fileName, titles, list, httpResponse);
	}

}
