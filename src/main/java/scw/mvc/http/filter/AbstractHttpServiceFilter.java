package scw.mvc.http.filter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import scw.core.utils.XUtils;
import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.FilterChain;
import scw.mvc.annotation.Controller;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpFilter;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;

public abstract class AbstractHttpServiceFilter extends HttpFilter {
	public abstract Action<Channel> getAction(HttpRequest request);

	public abstract void scanning(Class<?> clz, Method method, Controller classController, Controller methodController,
			Action<Channel> action);

	@Override
	public Object doFilter(HttpChannel channel, HttpRequest httpRequest,
			HttpResponse httpResponse, FilterChain chain) throws Throwable {
		Action<Channel> action = getAction(httpRequest);
		if (action == null) {
			return chain.doFilter(channel);
		}

		return action.doAction(channel);
	}
	
	public static RestInfo getRestInfo(Action<Channel> action, Class<?> clz, java.lang.reflect.Method method) {
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
