package scw.mvc.action.filter;

import scw.mvc.Channel;
import scw.mvc.action.Action;

public interface ActionFilter {
	Object doFilter(Channel channel, Action action, ActionFilterChain chain) throws Throwable;
}
