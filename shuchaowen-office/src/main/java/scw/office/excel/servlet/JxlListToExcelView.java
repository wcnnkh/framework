package scw.office.excel.servlet;

import java.io.IOException;
import java.util.List;

import scw.mvc.HttpChannel;
import scw.mvc.view.View;
import scw.office.excel.jxl.export.JxlExport;

public class JxlListToExcelView implements View {
	private String fileName;
	private String[] titles;
	private List<Object[]> list;

	public JxlListToExcelView(String fileName, String[] titles, List<Object[]> list) {
		this.fileName = fileName;
		this.titles = titles;
		this.list = list;
	}

	public void render(HttpChannel httpChannel) throws IOException {
		if (httpChannel.getResponse().getContentType() == null) {
			httpChannel.getResponse().setContentType("text/html;charset=" + httpChannel.getResponse().getCharacterEncoding());
		}

		JxlExport.exportExcel(fileName, titles, list, httpChannel.getResponse());
	}

}
