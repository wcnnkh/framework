package scw.mvc.http.view;

import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.http.HttpView;

public class HttpCode extends HttpView {
	private int status;
	private String msg;

	public HttpCode(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	@Override
	public void render(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse) throws Throwable {
		if (httpResponse.getContentType() == null) {
			httpResponse.setContentType("text/html;charset=" + httpResponse.getCharacterEncoding());
		}

		if (channel.isLogEnabled()) {
			channel.log("path={},method={},status={},msg={}", httpRequest.getController(), httpRequest.getMethod(),
					status, msg);
		}
		httpResponse.sendError(status, msg);
	}
}
