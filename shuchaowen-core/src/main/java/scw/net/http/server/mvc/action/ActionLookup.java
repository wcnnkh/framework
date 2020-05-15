package scw.net.http.server.mvc.action;

import scw.net.http.server.mvc.HttpChannel;

public interface ActionLookup {
	Action lookup(HttpChannel httpChannel);

	void register(Action action);
}
