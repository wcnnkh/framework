package scw.core.aop;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import scw.core.instance.InstanceFactory;
import scw.core.utils.CollectionUtils;

public final class InstanceFactoryFilterChain implements FilterChain {
	private Iterator<String> iterator;
	private final InstanceFactory instanceFactory;
	private Filter lastFilter;

	public InstanceFactoryFilterChain(InstanceFactory instanceFactory, Collection<String> filterNames,
			Filter lastFilter) {
		this.instanceFactory = instanceFactory;
		if (!CollectionUtils.isEmpty(filterNames)) {
			iterator = filterNames.iterator();
		}
		this.lastFilter = lastFilter;
	}

	public Object doFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args) throws Throwable {
		if (iterator == null) {
			return lastFilter(invoker, proxy, targetClass, method, args);
		} else if (iterator.hasNext()) {
			Filter filter = instanceFactory.getInstance(iterator.next());
			return filter.filter(invoker, proxy, targetClass, method, args, this);
		} else {
			return lastFilter(invoker, proxy, targetClass, method, args);
		}
	}

	private Object lastFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args) throws Throwable {
		Object value = lastFilter == null ? invoker.invoke(args)
				: lastFilter.filter(invoker, proxy, targetClass, method, args, this);
		lastFilter = null;
		return value;
	}

}
