package scw.mvc.action.manager;

import java.util.LinkedList;

import scw.mvc.Channel;
import scw.mvc.action.Action;

public class MultiActionLookup extends LinkedList<ActionLookup> implements
		ActionLookup {
	private static final long serialVersionUID = 1L;

	public Action lookup(Channel channel) {
		for (ActionLookup lookup : this) {
			Action action = lookup.lookup(channel);
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
