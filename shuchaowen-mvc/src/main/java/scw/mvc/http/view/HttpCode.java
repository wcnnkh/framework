package scw.mvc.http.view;

import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpView;
import scw.mvc.http.ServerHttpRequest;
import scw.mvc.http.ServerHttpResponse;

public class HttpCode extends HttpView {
	private int status;
	private String msg;

	public HttpCode(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	@Override
	public void render(HttpChannel channel, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) throws Throwable {
		if (serverHttpResponse.getContentType() == null) {
			serverHttpResponse.setContentType("text/html;charset=" + serverHttpResponse.getCharacterEncoding());
		}

		if (channel.isLogEnabled()) {
			channel.log("path={},method={},status={},msg={}", serverHttpRequest.getController(), serverHttpRequest.getMethod(),
					status, msg);
		}
		serverHttpResponse.sendError(status);
	}
}
