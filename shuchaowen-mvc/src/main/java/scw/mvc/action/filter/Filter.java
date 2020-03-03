package scw.mvc.action.filter;

import scw.mvc.Channel;
import scw.mvc.action.Action;

public interface Filter {
	Object doFilter(Channel channel, Action action, FilterChain chain) throws Throwable;
}
