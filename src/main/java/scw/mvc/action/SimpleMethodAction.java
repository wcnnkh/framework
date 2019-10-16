package scw.mvc.action;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

import scw.core.PropertyFactory;
import scw.core.instance.InstanceFactory;
import scw.core.reflect.AnnotationFactory;
import scw.mvc.Channel;
import scw.mvc.Filter;
import scw.mvc.FilterChain;
import scw.mvc.MVCUtils;
import scw.mvc.SimpleFilterChain;

public class SimpleMethodAction implements FilterAction, MethodAction {
	private final InvokerAction action;
	private final Collection<Filter> filters;
	private final Collection<ActionFilter> actionFilters;

	public SimpleMethodAction(InstanceFactory instanceFactory, PropertyFactory propertyFactory, Class<?> clazz,
			Method method, AnnotationFactory superAnnotationFactory) {
		this.action = new InvokerAction(instanceFactory, propertyFactory, clazz, method, superAnnotationFactory);
		this.filters = MVCUtils.getControllerFilter(Filter.class, clazz, method, instanceFactory);
		this.actionFilters = MVCUtils.getControllerFilter(ActionFilter.class, clazz, method, instanceFactory);
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
		return action.getMethod();
	}

	public Collection<ActionFilter> getActionFilters() {
		return Collections.unmodifiableCollection(actionFilters);
	}

	public <T extends Annotation> T getAnnotation(Class<T> type) {
		return action.getAnnotation(type);
	}

	public Class<?> getTargetClass() {
		return action.getTargetClass();
	}
}
