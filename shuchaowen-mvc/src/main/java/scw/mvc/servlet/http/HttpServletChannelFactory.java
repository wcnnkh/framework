package scw.mvc.servlet.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.annotation.AutoImpl;
import scw.net.http.server.mvc.HttpChannel;

@AutoImpl(ConfigurationHttpServletChannelFactory.class)
public interface HttpServletChannelFactory {
	HttpChannel getHttpChannel(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
}
