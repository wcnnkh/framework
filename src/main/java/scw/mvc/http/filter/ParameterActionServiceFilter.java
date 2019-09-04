package scw.mvc.http.filter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import scw.core.exception.AlreadyExistsException;
import scw.core.utils.XUtils;
import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;
import scw.mvc.annotation.Controller;
import scw.mvc.http.HttpRequest;

public final class ParameterActionServiceFilter extends AbstractHttpServiceFilter {
	private final Map<String, Map<String, Map<String, Action<Channel>>>> actionMap = new HashMap<String, Map<String, Map<String, Action<Channel>>>>();
	private String key;

	public ParameterActionServiceFilter(String key) {
		this.key = key;
	}

	@Override
	public Action<Channel> getAction(HttpRequest request) {
		if (key == null) {
			return null;
		}

		Map<String, Map<String, Action<Channel>>> map = actionMap.get(request.getRequestPath());
		if (map == null) {
			return null;
		}

		Map<String, Action<Channel>> methodMap = map.get(request.getMethod());
		if (methodMap == null) {
			return null;
		}

		String action = request.getParameter(key);
		if (action == null) {
			return null;
		}
		return methodMap.get(action);
	}

	@Override
	public void scanning(Class<?> clz, Method method, Controller classController, Controller methodController,
			Action<Channel> action) {
		String clzPath = classController == null ? "" : classController.value();
		String path = XUtils.mergePath("/", clzPath);
		Map<String, Map<String, Action<Channel>>> clzMap = actionMap.get(path);
		if (clzMap == null) {
			clzMap = new HashMap<String, Map<String, Action<Channel>>>();
		}

		String actionName = methodController.value();
		if ("".equals(actionName)) {
			actionName = method.getName();
		}

		scw.net.http.Method[] types = MVCUtils.mergeRequestType(clz, method);
		for (scw.net.http.Method type : types) {
			Map<String, Action<Channel>> map = clzMap.get(type.name());
			if (map == null) {
				map = new HashMap<String, Action<Channel>>();
			}

			if (map.containsKey(actionName)) {
				throw new AlreadyExistsException(MVCUtils.getExistActionErrMsg(action, map.get(actionName)));
			}

			map.put(actionName, action);
			clzMap.put(type.name(), map);
			actionMap.put(path, clzMap);
		}
	}
}
