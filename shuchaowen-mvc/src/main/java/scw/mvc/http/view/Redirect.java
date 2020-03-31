package scw.mvc.http.view;

import scw.core.utils.StringUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.http.HttpView;

public class Redirect extends HttpView {
	private static final String ROOT_PATH = "/";
	
	private String url;

	public Redirect(String url) {
		this.url = url;
	}

	@Override
	public void render(HttpChannel channel, HttpRequest httpRequest,
			HttpResponse httpResponse) throws Throwable {
		String redirect = url;
		if(StringUtils.isEmpty(redirect) || ROOT_PATH.equals(url)){
			redirect = httpRequest.getContextPath();
		} else if(redirect.startsWith(ROOT_PATH) && !redirect.startsWith(httpRequest.getContextPath())){
			redirect = httpRequest.getContextPath() + redirect;
		}
		
		if (channel.isLogEnabled()) {
			channel.log("redirect:{}", redirect);
		}
		httpResponse.sendRedirect(redirect);
	}

}
