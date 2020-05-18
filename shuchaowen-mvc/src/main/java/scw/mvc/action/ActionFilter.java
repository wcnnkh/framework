package scw.mvc.action;

import scw.mvc.HttpChannel;

public interface ActionFilter {
	Object doFilter(HttpChannel httpChannel, Action action, ActionService service) throws Throwable;
}
