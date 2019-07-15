package scw.servlet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.aop.Invoker;
import scw.core.exception.ParameterException;
import scw.core.utils.ClassUtils;
import scw.servlet.annotation.Controller;
import scw.servlet.annotation.Filters;
import scw.servlet.annotation.Methods;

public final class MethodAction implements Action {
	private final MethodParameter[] methodParameters;
	private final Collection<Filter> filters;
	private final Invoker invoker;

	public MethodAction(BeanFactory beanFactory, Class<?> clz, Method method) {
		this.methodParameters = getMethodParameter(method);
		this.filters = mergeFilter(clz, method, beanFactory);
		this.invoker = BeanUtils.getInvoker(beanFactory, clz, method);
	}

	public void doAction(Request request, Response response) throws Throwable {
		FilterChain filterChain = new IteratorFilterChain(filters, new RealAction());
		filterChain.doFilter(request, response);
	}

	final class RealAction implements Action {
		public void doAction(Request request, Response response) throws Throwable {
			Object[] args = new Object[methodParameters.length];
			for (int i = 0; i < methodParameters.length; i++) {
				args[i] = methodParameters[i].getParameter(request, response);
			}

			response.write(invoker.invoke(args));
		}
	}

	private MethodParameter[] getMethodParameter(Method method) {
		String[] tempKeys = ClassUtils.getParameterName(method);
		Class<?>[] types = method.getParameterTypes();
		MethodParameter[] paramInfos = new MethodParameter[types.length];
		for (int l = 0; l < types.length; l++) {
			paramInfos[l] = new MethodParameter(types[l], tempKeys[l]);
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

	public static scw.core.net.http.Method[] mergeRequestType(Class<?> clz, Method method) {
		Controller clzController = clz.getAnnotation(Controller.class);
		Controller methodController = method.getAnnotation(Controller.class);
		if (clzController == null || methodController == null) {
			throw new ParameterException("方法或类上都不存在Controller注解");
		}

		Methods methods = method.getAnnotation(Methods.class);

		Map<String, scw.core.net.http.Method> requestTypeMap = new HashMap<String, scw.core.net.http.Method>();
		if (methods == null) {
			if (clzController != null) {
				for (scw.core.net.http.Method requestType : clzController.methods()) {
					requestTypeMap.put(requestType.name(), requestType);
				}
			}
		} else {
			for (scw.core.net.http.Method requestType : methods.value()) {
				requestTypeMap.put(requestType.name(), requestType);
			}
		}

		if (methodController != null) {
			for (scw.core.net.http.Method requestType : methodController.methods()) {
				requestTypeMap.put(requestType.name(), requestType);
			}
		}

		if (requestTypeMap.size() == 0) {
			requestTypeMap.put(scw.core.net.http.Method.GET.name(), scw.core.net.http.Method.GET);
		}

		return requestTypeMap.values().toArray(new scw.core.net.http.Method[0]);
	}

	@Override
	public String toString() {
		return invoker.toString();
	}
}

final class MethodParameter {
	private final Class<?> type;
	private final String name;

	public MethodParameter(Class<?> type, String name) {
		this.type = type;
		this.name = name;
	}

	public Object getParameter(Request request, ServletResponse response) {
		if (ServletRequest.class.isAssignableFrom(type)) {
			return request;
		} else if (ServletResponse.class.isAssignableFrom(type)) {
			return response;
		} else {
			try {
				return request.getParameter(type, name);
			} catch (Exception e) {
				throw new ParameterException(e, "解析参数错误name=" + name + ",type=" + type.getName());
			}
		}
	}
}
