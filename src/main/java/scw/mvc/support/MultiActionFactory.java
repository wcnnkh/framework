package scw.mvc.support;

import java.util.LinkedList;

import scw.mvc.Action;
import scw.mvc.Channel;

public final class MultiActionFactory extends LinkedList<ActionFactory> implements ActionFactory {
	private static final long serialVersionUID = 1L;

	public Action getAction(Channel channel) {
		for (ActionFactory actionFactory : this) {
			if (actionFactory == null) {
				continue;
			}

			Action action = actionFactory.getAction(channel);
			if (action != null) {
				return action;
			}
		}
		return null;
	}
}
