package scw.mvc.http.view;

import java.util.LinkedList;

import scw.mvc.Channel;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.util.CSVUtils;

public final class CSVView extends LinkedList<Object[]> implements scw.mvc.View {
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

	public void render(HttpRequest request, HttpResponse response) throws Exception {
		response.setContentType("text/csv");
		response.getHeaders().set("Content-Disposition",
				"attachment;filename=" + fileNameConver(getFileName()) + ".csv");
		CSVUtils.write(response.getWriter(), getSplit(), isRemoveLineBreaks(), this);
	}

	public void render(Channel channel) throws Throwable {
		HttpChannel httpChannel = (HttpChannel) channel;
		render((HttpRequest) httpChannel.getRequest(), (HttpResponse) httpChannel.getResponse());
	}

}
