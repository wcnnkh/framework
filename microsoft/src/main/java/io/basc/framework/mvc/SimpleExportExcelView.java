package io.basc.framework.mvc;

import java.io.IOException;
import java.util.List;

import io.basc.framework.microsoft.ExcelExport;
import io.basc.framework.microsoft.MicrosoftUtils;
import io.basc.framework.mvc.view.View;

public class SimpleExportExcelView implements View {
	private String fileName;
	private String[] titles;
	private List<String[]> list;

	public SimpleExportExcelView(String fileName, String[] titles, List<String[]> list) {
		this.fileName = fileName;
		this.titles = titles;
		this.list = list;
	}

	public void render(HttpChannel httpChannel) throws IOException {
		ExcelExport excelExport = null;
		try {
			excelExport = MicrosoftUtils.createExcelExport(httpChannel.getResponse(), fileName + ".xls");
			excelExport.append(titles);
			for (String[] contents : list) {
				excelExport.append(contents);
			}
			excelExport.flush();
		} finally {
			excelExport.close();
		}
	}

}
