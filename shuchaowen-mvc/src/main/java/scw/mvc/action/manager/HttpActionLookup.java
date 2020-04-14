package scw.mvc.action.manager;

import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.http.HttpChannel;

public abstract class HttpActionLookup implements ActionLookup {
	public final Action lookup(Channel channel) {
		if (channel instanceof HttpChannel) {
			return lookup((HttpChannel) channel);
		}
		return null;
	}

	protected abstract Action lookup(HttpChannel channel);

	public final void register(Action action) {
		if (action instanceof HttpAction) {
			register((HttpAction) action);
		}
	}

	protected abstract void register(HttpAction action);
}
