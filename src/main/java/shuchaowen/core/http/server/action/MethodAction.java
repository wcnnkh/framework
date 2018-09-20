package shuchaowen.core.http.server.action;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.http.server.Action;
import shuchaowen.core.http.server.ActionFilterChain;
import shuchaowen.core.http.server.Filter;
import shuchaowen.core.http.server.FilterChain;
import shuchaowen.core.http.server.MethodParameter;
import shuchaowen.core.http.server.Request;
import shuchaowen.core.http.server.Response;
import shuchaowen.core.http.server.annotation.Controller;
import shuchaowen.core.http.server.annotation.Filters;
import shuchaowen.core.http.server.annotation.Methods;
import shuchaowen.core.invoke.Invoker;
import shuchaowen.core.invoke.ReflectInvoker;
import shuchaowen.core.util.ClassUtils;

public class MethodAction implements Action {
	private List<Filter> filterList;
	private Invoker invoke;
	private MethodParameter[] paramsInfo;

	public MethodAction(BeanFactory beanFactory, Class<?> clz, Method method) {
		this.invoke = new ReflectInvoker(beanFactory, clz, method);
		Controller clzController = clz.getAnnotation(Controller.class);
		Controller methodControler = method.getAnnotation(Controller.class);
		this.paramsInfo = getMethodParameter(method);
		this.filterList = mergeFilter(beanFactory, clzController, method.getAnnotation(Filters.class), methodControler);
	}

	public void doAction(Request request, Response response) throws Throwable {
		FilterChain chain = new ActionFilterChain(invoke, paramsInfo, filterList);
		chain.doFilter(request, response);
	}

	@Override
	public String toString() {
		return invoke.toString();
	}

	public static MethodParameter[] getMethodParameter(Method method) {
		String[] tempKeys = ClassUtils.getParameterName(method);
		Class<?>[] types = method.getParameterTypes();
		MethodParameter[] paramInfos = new MethodParameter[types.length];
		for (int l = 0; l < types.length; l++) {
			paramInfos[l] = new MethodParameter(types[l], tempKeys[l]);
		}
		return paramInfos;
	}

	public static List<Filter> mergeFilter(BeanFactory beanFactory, Controller clzController, Filters filters,
			Controller methodController) {
		Map<String, Boolean> nameMap = new HashMap<String, Boolean>();
		List<Filter> list = new ArrayList<Filter>();

		if (filters == null) {
			if (clzController != null) {
				for (Class<? extends Filter> filter : clzController.filters()) {
					String name = filter.getName();
					if (nameMap.containsKey(name)) {
						continue;
					}

					nameMap.put(name, true);
					list.add(beanFactory.get(filter));
				}
			}
		} else {
			for (Class<? extends Filter> filter : filters.value()) {
				String name = filter.getName();
				if (nameMap.containsKey(name)) {
					continue;
				}

				nameMap.put(name, true);
				list.add(beanFactory.get(filter));
			}
		}

		if (methodController != null) {
			for (Class<? extends Filter> filter : methodController.filters()) {
				String name = filter.getName();
				if (nameMap.containsKey(name)) {
					continue;
				}

				nameMap.put(name, true);
				list.add(beanFactory.get(filter));
			}
		}
		return list;
	}

	public static shuchaowen.core.http.enums.Method[] mergeRequestType(Class<?> clz, Method method) {
		Controller clzController = clz.getAnnotation(Controller.class);
		Controller methodController = method.getAnnotation(Controller.class);
		if (clzController == null || methodController == null) {
			throw new ShuChaoWenRuntimeException();
		}

		Methods methods = method.getAnnotation(Methods.class);

		Map<String, shuchaowen.core.http.enums.Method> requestTypeMap = new HashMap<String, shuchaowen.core.http.enums.Method>();
		if (methods == null) {
			if (clzController != null) {
				for (shuchaowen.core.http.enums.Method requestType : clzController.methods()) {
					requestTypeMap.put(requestType.name(), requestType);
				}
			}
		} else {
			for (shuchaowen.core.http.enums.Method requestType : methods.value()) {
				requestTypeMap.put(requestType.name(), requestType);
			}
		}

		if (methodController != null) {
			for (shuchaowen.core.http.enums.Method requestType : methodController.methods()) {
				requestTypeMap.put(requestType.name(), requestType);
			}
		}

		if (requestTypeMap.size() == 0) {
			requestTypeMap.put(shuchaowen.core.http.enums.Method.GET.name(), shuchaowen.core.http.enums.Method.GET);
		}

		return requestTypeMap.values().toArray(new shuchaowen.core.http.enums.Method[0]);
	}
}
