package scw.mvc.action;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.Filter;
import scw.mvc.FilterChain;
import scw.mvc.InvokerAction;
import scw.mvc.MVCUtils;
import scw.mvc.SimpleFilterChain;

public class MethodAction implements FilterAction {
	private final Action<Channel> action;
	private final Collection<Filter> filters;
	private final Method method;
	private final Class<?> clazz;
	private final Collection<ActionFilter> actionFilters;

	public MethodAction(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> clazz, Method method) {
		this.method = method;
		this.clazz = clazz;
		this.action = new InvokerAction(beanFactory, propertyFactory, clazz, method);
		this.filters = MVCUtils.getControllerFilter(Filter.class, clazz, method, beanFactory);
		this.actionFilters = MVCUtils.getControllerFilter(ActionFilter.class, clazz, method, beanFactory);
	}

	public Object doAction(Channel channel) throws Throwable {
		FilterChain filterChain = new SimpleFilterChain(filters, action);
		return filterChain.doFilter(channel);
	}

	@Override
	public String toString() {
		return action.toString();
	}

	public Method getMethod() {
		return method;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public Collection<ActionFilter> getActionFilters() {
		return Collections.unmodifiableCollection(actionFilters);
	}
}
