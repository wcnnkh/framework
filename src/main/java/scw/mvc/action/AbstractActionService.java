package scw.mvc.action;

import java.util.Collection;
import java.util.LinkedList;

import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.FilterChain;

public abstract class AbstractActionService implements ActionService {

	public abstract Collection<ActionFilter> getActionFilters();

	public Object doFilter(Channel channel, FilterChain chain) throws Throwable {
		Action<Channel> action = getAction(channel);
		if (action == null) {
			return chain.doFilter(channel);
		}

		Collection<ActionFilter> actionFilters;
		if (action instanceof FilterAction) {
			actionFilters = new LinkedList<ActionFilter>();
			actionFilters.addAll(getActionFilters());
			actionFilters.addAll(((FilterAction) action).getActionFilters());
		} else {
			actionFilters = getActionFilters();
		}

		ActionFilterChain actionFilterChain = new SimpleActionFilterChain(actionFilters);
		return actionFilterChain.doFilter(action, channel);
	}

}
