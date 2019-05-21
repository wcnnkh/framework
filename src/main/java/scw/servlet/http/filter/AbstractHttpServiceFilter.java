package scw.servlet.http.filter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import scw.core.utils.XUtils;
import scw.servlet.Action;
import scw.servlet.Filter;
import scw.servlet.FilterChain;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.ServletUtils;
import scw.servlet.annotation.Controller;

abstract class AbstractHttpServiceFilter implements Filter {
	public abstract Action getAction(HttpServletRequest request);

	public abstract void scanning(Class<?> clz, Method method, Controller classController, Controller methodController,
			Action action);

	public void doFilter(Request request, Response response, FilterChain filterChain) throws Throwable {
		if (!ServletUtils.isHttpServlet(request, response)) {
			filterChain.doFilter(request, response);
			return;
		}

		Action action = getAction((HttpServletRequest) request);
		if (action == null) {
			filterChain.doFilter(request, response);
			return;
		}

		action.doAction(request, response);
	}

	public static RestInfo getRestInfo(Action action, Class<?> clz, java.lang.reflect.Method method) {
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
		restUrl.setAction(action);
		return restUrl;
	}
}
