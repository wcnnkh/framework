package scw.mvc.http.view;

import scw.core.utils.StringUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.ServerHttpRequest;
import scw.mvc.http.ServerHttpResponse;
import scw.mvc.http.HttpView;

public class Redirect extends HttpView {
	private static final String ROOT_PATH = "/";
	
	private String url;

	public Redirect(String url) {
		this.url = url;
	}

	@Override
	public void render(HttpChannel channel, ServerHttpRequest serverHttpRequest,
			ServerHttpResponse serverHttpResponse) throws Throwable {
		String redirect = url;
		if(StringUtils.isEmpty(redirect) || ROOT_PATH.equals(url)){
			redirect = serverHttpRequest.getContextPath();
		} else if(redirect.startsWith(ROOT_PATH) && !redirect.startsWith(serverHttpRequest.getContextPath())){
			redirect = serverHttpRequest.getContextPath() + redirect;
		}
		
		if (channel.isLogEnabled()) {
			channel.log("redirect:{}", redirect);
		}
		serverHttpResponse.sendRedirect(redirect);
	}

}
