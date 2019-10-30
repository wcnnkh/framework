package scw.mvc.action;

import java.lang.reflect.Method;
import java.util.Collection;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.core.reflect.AnnotationFactory;
import scw.mvc.Channel;
import scw.mvc.Filter;
import scw.mvc.FilterChain;
import scw.mvc.MVCUtils;
import scw.mvc.SimpleFilterChain;

public class SimpleMethodAction extends AbstractMethodActionWrapper implements FilterAction {
	private final InvokerAction action;
	private final Collection<Filter> filters;
	private final Collection<ActionFilter> actionFilters;

	public SimpleMethodAction(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> clazz,
			Method method, AnnotationFactory superAnnotationFactory) {
		this.action = new InvokerAction(beanFactory, propertyFactory, clazz, method, superAnnotationFactory);
		this.filters = MVCUtils.getControllerFilter(Filter.class, clazz, method, beanFactory);
		this.actionFilters = MVCUtils.getControllerFilter(ActionFilter.class, clazz, method, beanFactory);
	}

	@Override
	public MethodAction getTargetMethodAction() {
		return action;
	}

	public Object doAction(Channel channel) throws Throwable {
		FilterChain filterChain = new SimpleFilterChain(filters, action);
		return filterChain.doFilter(channel);
	}

	public Collection<ActionFilter> getActionFilters() {
		return actionFilters;
	}
}
