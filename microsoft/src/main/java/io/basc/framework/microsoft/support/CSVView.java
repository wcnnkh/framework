package io.basc.framework.microsoft.support;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import io.basc.framework.http.HttpUtils;
import io.basc.framework.mvc.HttpChannel;

public class CSVView extends ArrayList<Object[]> implements io.basc.framework.mvc.view.View {
	private static final long serialVersionUID = 1L;
	private String fileName;
	private Charset charset;

	public CSVView(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public void render(HttpChannel httpChannel) throws IOException {
		HttpUtils.writeFileMessageHeaders(httpChannel.getResponse(), getFileName() + ".csv", charset);
		CSVPrinter csvPrinter = new CSVPrinter(httpChannel.getResponse().getWriter(), CSVFormat.DEFAULT);
		for (Object[] values : this) {
			csvPrinter.printRecord(values);
		}
		csvPrinter.flush();
		csvPrinter.close();
	}
}
