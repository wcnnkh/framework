package scw.net.http.server.mvc.action;

import java.util.LinkedList;

import scw.net.http.server.mvc.HttpChannel;

public class MultiActionLookup extends LinkedList<ActionLookup> implements
		ActionLookup {
	private static final long serialVersionUID = 1L;

	public Action lookup(HttpChannel httpChannel) {
		for (ActionLookup lookup : this) {
			Action action = lookup.lookup(httpChannel);
			if (action != null) {
				return action;
			}
		}
		return null;
	}

	public void register(Action action) {
		for (ActionLookup lookup : this) {
			lookup.register(action);
		}
	}
}
