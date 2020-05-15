package scw.net.http.server.mvc.view;

import java.io.IOException;

import scw.core.utils.StringUtils;
import scw.net.http.server.mvc.HttpChannel;

public class Redirect implements View {
	private static final String ROOT_PATH = "/";
	
	private String url;

	public Redirect(String url) {
		this.url = url;
	}

	public void render(HttpChannel httpChannel) throws IOException {
		String redirect = url;
		if(StringUtils.isEmpty(redirect) || ROOT_PATH.equals(url)){
			redirect = httpChannel.getRequest().getContextPath();
		} else if(redirect.startsWith(ROOT_PATH) && !redirect.startsWith(httpChannel.getRequest().getContextPath())){
			redirect = httpChannel.getRequest().getContextPath() + redirect;
		}
		
		if (httpChannel.isLogEnabled()) {
			httpChannel.log("redirect:{}", redirect);
		}
		httpChannel.getResponse().sendRedirect(redirect);
	}

}
