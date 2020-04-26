package scw.mvc.action;

import scw.core.instance.annotation.Configuration;
import scw.mvc.Channel;
import scw.mvc.action.manager.ActionLookupManager;
import scw.mvc.action.notfound.NotFoundService;
import scw.mvc.service.Filter;
import scw.mvc.service.FilterChain;
import scw.mvc.service.context.ContextManager;

@Configuration(order = Integer.MIN_VALUE)
public final class ActionServiceFilter implements Filter {
	private ActionLookupManager actionLookupManager;
	private NotFoundService notFoundService;

	public ActionServiceFilter(ActionLookupManager actionLookupManager,
			NotFoundService notFoundService) {
		this.actionLookupManager = actionLookupManager;
		this.notFoundService = notFoundService;
	}

	public Object doFilter(Channel channel, FilterChain chain) throws Throwable {
		Action action = actionLookupManager.lookup(channel);
		if (action == null) {
			return notFoundService.notfound(channel, chain);
		}

		return doAction(channel, action, chain);
	}

	protected Object doAction(Channel channel, Action action, FilterChain chain)
			throws Throwable {
		return ContextManager.doFilter(channel, action,
				action.getActionFilterChain());
	}
}
