package scw.mvc.action.manager;

import scw.mvc.Channel;
import scw.mvc.action.Action;

public interface ActionLookup {
	Action lookup(Channel channel);

	void register(Action action);
}
