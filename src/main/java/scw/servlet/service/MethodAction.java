package scw.servlet.service;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.common.exception.ParameterException;
import scw.common.utils.ClassUtils;
import scw.reflect.Invoker;
import scw.servlet.Filter;
import scw.servlet.FilterChain;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.annotation.Controller;
import scw.servlet.annotation.Filters;
import scw.servlet.annotation.Methods;

public class MethodAction implements Action {
	private final MethodParameter[] methodParameters;
	private final Collection<Filter> filters;
	private final Invoker invoker;

	public MethodAction(BeanFactory beanFactory, Class<?> clz, Method method) {
		this.methodParameters = getMethodParameter(method);
		this.filters = mergeFilter(clz, method, beanFactory);
		this.invoker = BeanUtils.getInvoker(beanFactory, clz, method);
	}

	public void doAction(Request request, Response response) throws Throwable {
		FilterChain filterChain = new ActionFilterChain(invoker, methodParameters, filters);
		filterChain.doFilter(request, response);
	}

	private MethodParameter[] getMethodParameter(Method method) {
		String[] tempKeys = ClassUtils.getParameterName(method);
		Class<?>[] types = method.getParameterTypes();
		Parameter[] parameters = method.getParameters();
		MethodParameter[] paramInfos = new MethodParameter[types.length];
		for (int l = 0; l < types.length; l++) {
			paramInfos[l] = new MethodParameter(types[l], parameters[l], tempKeys[l]);
		}
		return paramInfos;
	}

	private Collection<Filter> mergeFilter(Class<?> clz, Method method, BeanFactory beanFactory) {
		Controller clzController = clz.getAnnotation(Controller.class);
		Controller methodController = method.getAnnotation(Controller.class);
		Filters filters = method.getAnnotation(Filters.class);

		LinkedHashSet<Filter> list = new LinkedHashSet<Filter>();
		if (filters == null) {
			if (clzController != null) {
				for (Class<? extends Filter> filter : clzController.filters()) {
					list.add(beanFactory.get(filter));
				}
			}
		} else {
			for (Class<? extends Filter> filter : filters.value()) {
				list.add(beanFactory.get(filter));
			}
		}

		if (methodController != null) {
			for (Class<? extends Filter> filter : methodController.filters()) {
				list.add(beanFactory.get(filter));
			}
		}
		
		return new ArrayList<Filter>(list);
	}

	public static scw.net.http.enums.Method[] mergeRequestType(Class<?> clz, Method method) {
		Controller clzController = clz.getAnnotation(Controller.class);
		Controller methodController = method.getAnnotation(Controller.class);
		if (clzController == null || methodController == null) {
			throw new ParameterException("方法或类上都不存在Controller注解");
		}

		Methods methods = method.getAnnotation(Methods.class);

		Map<String, scw.net.http.enums.Method> requestTypeMap = new HashMap<String, scw.net.http.enums.Method>();
		if (methods == null) {
			if (clzController != null) {
				for (scw.net.http.enums.Method requestType : clzController.methods()) {
					requestTypeMap.put(requestType.name(), requestType);
				}
			}
		} else {
			for (scw.net.http.enums.Method requestType : methods.value()) {
				requestTypeMap.put(requestType.name(), requestType);
			}
		}

		if (methodController != null) {
			for (scw.net.http.enums.Method requestType : methodController.methods()) {
				requestTypeMap.put(requestType.name(), requestType);
			}
		}

		if (requestTypeMap.size() == 0) {
			requestTypeMap.put(scw.net.http.enums.Method.GET.name(), scw.net.http.enums.Method.GET);
		}

		return requestTypeMap.values().toArray(new scw.net.http.enums.Method[0]);
	}
	
	@Override
	public String toString() {
		return invoker.toString();
	}
}
