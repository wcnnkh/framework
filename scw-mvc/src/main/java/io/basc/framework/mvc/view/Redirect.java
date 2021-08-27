package io.basc.framework.mvc.view;

import io.basc.framework.core.utils.StringUtils;
import io.basc.framework.mvc.HttpChannel;

import java.io.IOException;

public class Redirect implements View {
	private static final String ROOT_PATH = "/";

	private String url;

	public Redirect(String url) {
		this.url = url;
	}

	public void render(HttpChannel httpChannel) throws IOException {
		String redirect = url;
		if (StringUtils.isEmpty(redirect) || ROOT_PATH.equals(url)) {
			redirect = httpChannel.getRequest().getContextPath();
		} else if (redirect.startsWith(ROOT_PATH) && !redirect.startsWith(httpChannel.getRequest().getContextPath())) {
			redirect = httpChannel.getRequest().getContextPath() + redirect;
		}
		httpChannel.getResponse().sendRedirect(redirect);
	}

	@Override
	public String toString() {
		return "redirect: " + url;
	}
}
