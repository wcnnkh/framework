package scw.mvc.view;

import java.io.IOException;

import scw.mvc.HttpChannel;

public class HttpCode implements View {
	private int status;
	private String msg;

	public HttpCode(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	public void render(HttpChannel httpChannel) throws IOException {
		if (httpChannel.getResponse().getContentType() == null) {
			httpChannel.getResponse()
					.setContentType("text/html;charset=" + httpChannel.getResponse().getCharacterEncoding());
		}

		if (httpChannel.isLogEnabled()) {
			httpChannel.log("path={},method={},status={},msg={}", httpChannel.getRequest().getPath(),
					httpChannel.getRequest().getMethod(), status, msg);
		}
		httpChannel.getResponse().sendError(status, msg);
	}
}
