package scw.mvc.action.filter;

import scw.mvc.Channel;
import scw.mvc.action.Action;

public interface ActionFilterChain {
	Object doFilter(Channel channel, Action action) throws Throwable;
}
