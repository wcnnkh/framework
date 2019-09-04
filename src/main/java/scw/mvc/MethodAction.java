package scw.mvc;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;

public final class MethodAction implements Action<Channel> {
	private final Action<Channel> action;
	private final Collection<Filter> filters;

	public MethodAction(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> clz, Method method) {
		this.action = new InvokerAction(beanFactory, propertyFactory, clz, method);
		this.filters = MVCUtils.getControllerFilter(clz, method, beanFactory);
	}

	public Object doAction(Channel channel) throws Throwable {
		FilterChain filterChain = new SimpleFilterChain(filters, action);
		return filterChain.doFilter(channel);
	}

	@Override
	public String toString() {
		return action.toString();
	}
}
