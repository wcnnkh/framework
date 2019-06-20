package scw.servlet.http.view;

import java.util.LinkedList;

import javax.servlet.http.HttpServletResponse;

import scw.core.utils.CSVUtils;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.View;

public final class CSVView extends LinkedList<Object[]> implements View {
	private static final long serialVersionUID = 1L;
	private String fileName;
	private char split;

	public CSVView(String fileName) {
		this(fileName, CSVUtils.DEFAULT_SPLIT);
	}

	public CSVView(String fileName, char split) {
		this.fileName = fileName;
		this.split = split;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void render(Request request, Response response) throws Exception {
		response.setContentType("text/csv");
		if (response instanceof HttpServletResponse) {
			((HttpServletResponse) response).setHeader("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes(), "ISO-8859-1") + ".csv");
		}
		CSVUtils.write(response.getWriter(), split, this);
	}

}
