package scw.servlet.service;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.beans.annotation.InitMethod;
import scw.common.utils.XUtils;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.annotation.Controller;

public abstract class AbstractService implements Service {
	private Collection<Class<?>> classes;

	public AbstractService(Collection<Class<?>> classes) {
		this.classes = classes;
	}

	public abstract Action getAction(Request request);

	public abstract void scanning(Class<?> clz, Method method, Controller classController, Controller methodController);

	@InitMethod
	public void init() {
		for (Class<?> clz : classes) {
			Controller clzController = clz.getAnnotation(Controller.class);
			if (clzController == null) {
				continue;
			}

			for (Method method : clz.getDeclaredMethods()) {
				Controller methodController = method.getAnnotation(Controller.class);
				if (methodController == null) {
					continue;
				}

				scanning(clz, method, clzController, methodController);
			}
		}
		classes = null;
	}

	public void service(Request request, Response response, ServiceChain serviceChain) throws Throwable {
		Action action = getAction(request);
		if (action == null) {
			serviceChain.service(request, response);
			return;
		}

		action.doAction(request, response);
	}
	
	public static RestInfo getRestInfo(BeanFactory beanFactory, Class<?> clz, java.lang.reflect.Method method) {
		Controller clzController = clz.getAnnotation(Controller.class);
		if (clzController == null) {
			return null;
		}

		Deprecated d = method.getAnnotation(Deprecated.class);
		if (d != null) {
			return null;
		}

		Controller methodController = method.getAnnotation(Controller.class);
		if (methodController == null) {
			return null;
		}

		String allPath = XUtils.mergePath("/", clzController.value(), methodController.value());
		String[] requestPathArr = allPath.split("/");
		Map<Integer, String> resultKeyMap = new HashMap<Integer, String>(4);
		StringBuilder newRequestPath = new StringBuilder(allPath.length());
		for (int i = 0; i < requestPathArr.length; i++) {
			String str = requestPathArr[i];
			if (str.startsWith("{") && str.endsWith("}")) {
				newRequestPath.append("*");
				resultKeyMap.put(i, str.substring(1, str.length() - 1));
			} else {
				newRequestPath.append(str);
			}

			if (i < requestPathArr.length - 1) {
				newRequestPath.append("/");
			}
		}

		if (allPath.endsWith("/")) {
			newRequestPath.append("/");
		}

		RestInfo restUrl = new RestInfo();
		restUrl.setUrl(newRequestPath.toString());
		restUrl.setRegArr(newRequestPath.toString().split("/"));
		restUrl.setKeyMap(resultKeyMap);
		restUrl.setAction(new MethodAction(beanFactory, clz, method));
		return restUrl;
	}
}
