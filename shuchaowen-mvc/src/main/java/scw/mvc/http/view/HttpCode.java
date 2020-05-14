package scw.mvc.http.view;

import scw.mvc.Channel;
import scw.mvc.http.HttpView;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;

public class HttpCode extends HttpView {
	private int status;
	private String msg;

	public HttpCode(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	@Override
	public void render(Channel channel, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) throws Throwable {
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
