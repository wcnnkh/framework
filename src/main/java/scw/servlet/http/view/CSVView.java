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
	private final char split;
	private final boolean removeLineBreaks;

	public CSVView(String fileName) {
		this(fileName, true);
	}

	public CSVView(String fileName, boolean removeLineBreaks) {
		this(fileName, CSVUtils.DEFAULT_SPLIT, removeLineBreaks);
	}

	public CSVView(String fileName, char split, boolean removeLineBreaks) {
		this.fileName = fileName;
		this.split = split;
		this.removeLineBreaks = removeLineBreaks;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public char getSplit() {
		return split;
	}

	public boolean isRemoveLineBreaks() {
		return removeLineBreaks;
	}

	protected String fileNameConver(String fileName) throws Exception {
		return new String(fileName.getBytes(), "ISO-8859-1");
	}

	public void render(Request request, Response response) throws Exception {
		response.setContentType("text/csv");
		if (response instanceof HttpServletResponse) {
			((HttpServletResponse) response).setHeader("Content-Disposition",
					"attachment;filename=" + fileNameConver(getFileName()) + ".csv");
		}
		CSVUtils.write(response.getWriter(), getSplit(), isRemoveLineBreaks(), this);
	}

}
