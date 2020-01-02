package scw.mvc.support;

import java.util.Collection;

import scw.mvc.AbstractFilterChain;
import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.Filter;

public class ActionFilterChain extends AbstractFilterChain {
	private final Action action;

	public ActionFilterChain(Collection<Filter> filters, Action action) {
		super(filters);
		this.action = action;
	}

	@Override
	protected Object lastFilter(Channel channel) throws Throwable {
		return action == null ? null : action.doAction(channel);
	}

}
