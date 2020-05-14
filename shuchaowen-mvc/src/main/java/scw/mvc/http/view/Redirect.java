package scw.mvc.http.view;

import scw.core.utils.StringUtils;
import scw.mvc.Channel;
import scw.mvc.http.HttpView;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;

public class Redirect extends HttpView {
	private static final String ROOT_PATH = "/";
	
	private String url;

	public Redirect(String url) {
		this.url = url;
	}

	@Override
	public void render(Channel channel, ServerHttpRequest serverHttpRequest,
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
