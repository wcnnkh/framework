package scw.mvc.view;

import java.io.IOException;
import java.util.ArrayList;

import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.mvc.HttpChannel;
import scw.util.CSVUtils;

public final class CSVView extends ArrayList<Object[]> implements scw.mvc.view.View {
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

	protected String fileNameConver(String fileName) throws IOException {
		return new String(fileName.getBytes(), "ISO-8859-1");
	}

	public void render(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		response.setContentType("text/csv");
		response.getHeaders().set("Content-Disposition",
				"attachment;filename=" + fileNameConver(getFileName()) + ".csv");
		CSVUtils.write(response.getWriter(), getSplit(), isRemoveLineBreaks(), this);
	}

	public void render(HttpChannel httpChannel) throws IOException {
		render((ServerHttpRequest) httpChannel.getRequest(), (ServerHttpResponse) httpChannel.getResponse());
	}

}
