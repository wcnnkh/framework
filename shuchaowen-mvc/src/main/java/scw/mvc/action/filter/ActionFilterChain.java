package scw.mvc.action.filter;

import scw.beans.annotation.AutoImpl;
import scw.mvc.Channel;
import scw.mvc.action.Action;

@AutoImpl(ConfigurationActionFilterChain.class)
public interface ActionFilterChain {
	Object doFilter(Channel channel, Action action) throws Throwable;
}
