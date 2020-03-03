package scw.servlet.mvc.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.annotation.AutoImpl;
import scw.mvc.http.HttpChannel;

@AutoImpl(ConfigurationHttpServletChannelFactory.class)
public interface HttpServletChannelFactory {
	HttpChannel getHttpChannel(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
}
