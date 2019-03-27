package scw.servlet.action;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.common.exception.ParameterException;
import scw.common.utils.ClassUtils;
import scw.servlet.Action;
import scw.servlet.Filter;
import scw.servlet.FilterChain;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.action.annotation.Controller;
import scw.servlet.action.annotation.Filters;
import scw.servlet.action.annotation.Methods;

public class MethodAction implements Action {
	private MethodParameter[] methodParameters;
	private BeanFactory beanFactory;
	private Class<?> clz;
	private Method method;
	private List<String> filters;

	public MethodAction(BeanFactory beanFactory, Class<?> clz, Method method) {
		this.beanFactory = beanFactory;
		this.clz = clz;
		this.method = method;
		this.methodParameters = getMethodParameter();
		this.filters = mergeFilter();
	}

	public void doAction(Request request, Response response) throws Throwable {
		FilterChain filterChain = new ActionFilterChain(beanFactory, clz, method, methodParameters, filters);
		filterChain.doFilter(request, response);
	}

	private MethodParameter[] getMethodParameter() {
		String[] tempKeys = ClassUtils.getParameterName(method);
		Class<?>[] types = method.getParameterTypes();
		Parameter[] parameters = method.getParameters();
		MethodParameter[] paramInfos = new MethodParameter[types.length];
		for (int l = 0; l < types.length; l++) {
			paramInfos[l] = new MethodParameter(types[l], parameters[l], tempKeys[l]);
		}
		return paramInfos;
	}

	private List<String> mergeFilter() {
		Controller clzController = clz.getAnnotation(Controller.class);
		Controller methodController = method.getAnnotation(Controller.class);
		Filters filters = method.getAnnotation(Filters.class);

		Map<String, Boolean> nameMap = new HashMap<String, Boolean>();
		List<String> list = new LinkedList<String>();

		if (filters == null) {
			if (clzController != null) {
				for (Class<? extends Filter> filter : clzController.filters()) {
					String name = filter.getName();
					if (nameMap.containsKey(name)) {
						continue;
					}

					nameMap.put(name, true);
					list.add(name);
				}
			}
		} else {
			for (Class<? extends Filter> filter : filters.value()) {
				String name = filter.getName();
				if (nameMap.containsKey(name)) {
					continue;
				}

				nameMap.put(name, true);
				list.add(name);
			}
		}

		if (methodController != null) {
			for (Class<? extends Filter> filter : methodController.filters()) {
				String name = filter.getName();
				if (nameMap.containsKey(name)) {
					continue;
				}

				nameMap.put(name, true);
				list.add(name);
			}
		}
		return list;
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
}
