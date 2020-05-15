package scw.net.http.server.mvc.action;

import scw.net.http.server.mvc.HttpChannel;

public interface ActionService {
	Object doAction(HttpChannel httpChannel, Action action) throws Throwable;
}
