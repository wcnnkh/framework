package scw.mvc.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.mvc.http.HttpChannel;

public interface HttpServletChannelFactory {
	HttpChannel getHttpChannel(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
}
