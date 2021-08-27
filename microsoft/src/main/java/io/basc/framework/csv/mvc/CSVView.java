package io.basc.framework.csv.mvc;

import io.basc.framework.http.HttpUtils;
import io.basc.framework.mvc.HttpChannel;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class CSVView extends ArrayList<Object[]> implements io.basc.framework.mvc.view.View {
	private static final long serialVersionUID = 1L;
	private String fileName;

	public CSVView(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void render(HttpChannel httpChannel) throws IOException {
		HttpUtils.writeFileMessageHeaders(httpChannel.getResponse(), getFileName() + ".csv");
		CSVPrinter csvPrinter = new CSVPrinter(httpChannel.getResponse().getWriter(), CSVFormat.DEFAULT);
		for (Object[] values : this) {
			csvPrinter.printRecord(values);
		}
		csvPrinter.flush();
		csvPrinter.close();
	}
}
