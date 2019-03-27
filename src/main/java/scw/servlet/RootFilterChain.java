package scw.servlet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import scw.beans.BeanFactory;
import scw.common.utils.CollectionUtils;

public class RootFilterChain implements FilterChain {
	private final Action action;
	private final BeanFactory beanFactory;
	private Iterator<String> iterator;
	private HashSet<Filter> cacheMap;

	public RootFilterChain(BeanFactory beanFactory, Action action, Collection<String> filters) {
		this.action = action;
		this.beanFactory = beanFactory;
		if (!CollectionUtils.isEmpty(filters)) {
			iterator = filters.iterator();
			cacheMap = new HashSet<Filter>(filters.size(), 1);
		}
	}

	public void doFilter(Request request, Response response) throws Throwable {
		if (iterator == null) {
			action.doAction(request, response);
			return;
		}

		if (iterator.hasNext()) {
			Filter filter = beanFactory.get(iterator.next());
			if (cacheMap.add(filter)) {
				filter.doFilter(request, response, this);
			} else {
				doFilter(request, response);
			}
		} else {
			action.doAction(request, response);
		}
	}
}
