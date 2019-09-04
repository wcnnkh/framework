package scw.mvc.http;

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
