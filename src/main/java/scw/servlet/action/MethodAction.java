package scw.servlet.action;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.reflect.Invoker;
import scw.common.reflect.ReflectInvoker;
import scw.common.utils.ClassUtils;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.action.annotation.Controller;
import scw.servlet.action.annotation.Filters;
import scw.servlet.action.annotation.Methods;

public class MethodAction implements Action {
	private final List<Filter> filterList;
	private final Invoker invoke;
	private final MethodParameter[] paramsInfo;

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
		Parameter[] parameters = method.getParameters();
		MethodParameter[] paramInfos = new MethodParameter[types.length];
		for (int l = 0; l < types.length; l++) {
			paramInfos[l] = new MethodParameter(types[l], parameters[l], tempKeys[l]);
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

	public static scw.net.http.enums.Method[] mergeRequestType(Class<?> clz, Method method) {
		Controller clzController = clz.getAnnotation(Controller.class);
		Controller methodController = method.getAnnotation(Controller.class);
		if (clzController == null || methodController == null) {
			throw new ShuChaoWenRuntimeException();
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
