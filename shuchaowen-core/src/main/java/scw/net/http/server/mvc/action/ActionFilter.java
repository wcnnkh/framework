package scw.net.http.server.mvc.action;

import scw.net.http.server.mvc.HttpChannel;

public interface ActionFilter {
	Object doFilter(HttpChannel httpChannel, Action action, ActionService service) throws Throwable;
}
