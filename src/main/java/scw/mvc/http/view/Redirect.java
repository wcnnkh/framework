package scw.mvc.http.view;

import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.http.HttpView;

public class Redirect extends HttpView {
	private String url;

	public Redirect(String url) {
		this.url = url;
	}

	@Override
	public void render(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse) throws Throwable {
		if (channel.isLogEnabled()) {
			channel.log("redirect:{}", url);
		}
		httpResponse.sendRedirect(url);
	}

}
