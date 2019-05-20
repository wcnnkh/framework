package scw.servlet;

import java.util.Collection;
import java.util.Iterator;

public final class DefaultFilterChain implements FilterChain {
	private final Iterator<Filter> iterator;
	private final Action action;

	public DefaultFilterChain(Collection<Filter> filters, Action action) {
		this.iterator = (filters == null || filters.isEmpty()) ? null : filters.iterator();
		this.action = action;
	}

	public void doFilter(Request request, Response response) throws Throwable {
		if (iterator == null) {
			if (action != null) {
				action.doAction(request, response);
			}
		} else {
			if (iterator.hasNext()) {
				iterator.next().doFilter(request, response, this);
			} else {
				if (action != null) {
					action.doAction(request, response);
				}
			}
		}
	};
}
