package scw.mvc.action;

import scw.mvc.Action;
import scw.mvc.Channel;

public interface ActionFilterChain {
	Object doFilter(Action<Channel> action, Channel channel) throws Throwable;
}
