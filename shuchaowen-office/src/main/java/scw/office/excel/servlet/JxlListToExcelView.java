package scw.office.excel.servlet;

import java.util.List;

import scw.mvc.ServerRequest;
import scw.mvc.ServerResponse;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.ServerHttpRequest;
import scw.mvc.http.ServerHttpResponse;
import scw.mvc.http.HttpView;
import scw.office.excel.jxl.export.JxlExport;

public class JxlListToExcelView extends HttpView {
	private String fileName;
	private String[] titles;
	private List<Object[]> list;

	public JxlListToExcelView(String fileName, String[] titles, List<Object[]> list) {
		this.fileName = fileName;
		this.titles = titles;
		this.list = list;
	}

	public void render(ServerRequest serverRequest, ServerResponse serverResponse) throws Exception {

	}

	@Override
	public void render(HttpChannel channel, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) throws Throwable {
		if (serverHttpResponse.getContentType() == null) {
			serverHttpResponse.setContentType("text/html;charset=" + serverHttpResponse.getCharacterEncoding());
		}

		JxlExport.exportExcel(fileName, titles, list, serverHttpResponse);
	}

}
