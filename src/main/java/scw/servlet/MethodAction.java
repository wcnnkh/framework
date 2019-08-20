package scw.servlet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.PropertyFactory;
import scw.core.exception.ParameterException;
import scw.servlet.annotation.Controller;
import scw.servlet.annotation.Filters;
import scw.servlet.annotation.Methods;

public final class MethodAction implements Action {
	private final Collection<Filter> filters;
	private final SimpleAction action;

	public MethodAction(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> clz, Method method) {
		this.filters = mergeFilter(clz, method, beanFactory);
		this.action = new SimpleAction(BeanUtils.getInvoker(beanFactory, clz, method),
				ServletUtils.getParameterParseFilters(beanFactory, propertyFactory, clz, method), method);
	}

	public void doAction(Request request, Response response) throws Throwable {
		FilterChain filterChain = new IteratorFilterChain(filters, action);
		filterChain.doFilter(request, response);
	}

	private Collection<Filter> mergeFilter(Class<?> clz, Method method, BeanFactory beanFactory) {
		Controller clzController = clz.getAnnotation(Controller.class);
		Controller methodController = method.getAnnotation(Controller.class);
		Filters filters = method.getAnnotation(Filters.class);

		LinkedHashSet<Filter> list = new LinkedHashSet<Filter>();
		if (filters == null) {
			if (clzController != null) {
				for (Class<? extends Filter> filter : clzController.filters()) {
					list.add(beanFactory.getInstance(filter));
				}
			}
		} else {
			for (Class<? extends Filter> filter : filters.value()) {
				list.add(beanFactory.getInstance(filter));
			}
		}

		if (methodController != null) {
			for (Class<? extends Filter> filter : methodController.filters()) {
				list.add(beanFactory.getInstance(filter));
			}
		}

		return new ArrayList<Filter>(list);
	}

	public static scw.net.http.Method[] mergeRequestType(Class<?> clz, Method method) {
		Controller clzController = clz.getAnnotation(Controller.class);
		Controller methodController = method.getAnnotation(Controller.class);
		if (clzController == null || methodController == null) {
			throw new ParameterException("方法或类上都不存在Controller注解");
		}

		Methods methods = method.getAnnotation(Methods.class);

		Map<String, scw.net.http.Method> requestTypeMap = new HashMap<String, scw.net.http.Method>();
		if (methods == null) {
			if (clzController != null) {
				for (scw.net.http.Method requestType : clzController.methods()) {
					requestTypeMap.put(requestType.name(), requestType);
				}
			}
		} else {
			for (scw.net.http.Method requestType : methods.value()) {
				requestTypeMap.put(requestType.name(), requestType);
			}
		}

		if (methodController != null) {
			for (scw.net.http.Method requestType : methodController.methods()) {
				requestTypeMap.put(requestType.name(), requestType);
			}
		}

		if (requestTypeMap.size() == 0) {
			requestTypeMap.put(scw.net.http.Method.GET.name(), scw.net.http.Method.GET);
		}

		return requestTypeMap.values().toArray(new scw.net.http.Method[0]);
	}

	@Override
	public String toString() {
		return action.toString();
	}
}
