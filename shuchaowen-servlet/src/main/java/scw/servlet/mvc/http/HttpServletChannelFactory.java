package scw.servlet.mvc.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.annotation.AutoImpl;
import scw.mvc.Channel;

@AutoImpl(ConfigurationHttpServletChannelFactory.class)
public interface HttpServletChannelFactory {
	Channel getHttpChannel(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
}
