package scw.mvc.servlet.http;

import javax.servlet.http.HttpServletRequest;

import scw.mvc.servlet.ServletChannel;

public interface HttpServletChannel extends ServletChannel {
	HttpServletRequest getHttpRequest();

	HttpServletRequest getHttpResponse();
}
