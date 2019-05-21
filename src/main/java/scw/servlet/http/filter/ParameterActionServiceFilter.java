package scw.servlet.http.filter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import scw.core.exception.AlreadyExistsException;
import scw.core.utils.XUtils;
import scw.servlet.Action;
import scw.servlet.MethodAction;
import scw.servlet.annotation.Controller;

final class ParameterActionServiceFilter extends AbstractHttpServiceFilter {
	private final Map<String, Map<String, Map<String, Action>>> actionMap = new HashMap<String, Map<String, Map<String, Action>>>();
	private String key;

	public ParameterActionServiceFilter(String key) {
		this.key = key;
	}

	@Override
	public Action getAction(HttpServletRequest request) {
		if (key == null) {
			return null;
		}

		Map<String, Map<String, Action>> map = actionMap.get(request.getServletPath());
		if (map == null) {
			return null;
		}

		Map<String, Action> methodMap = map.get(request.getMethod());
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
			Action action) {
		String clzPath = classController == null ? "" : classController.value();
		String path = XUtils.mergePath("/", clzPath);
		Map<String, Map<String, Action>> clzMap = actionMap.get(path);
		if (clzMap == null) {
			clzMap = new HashMap<String, Map<String, Action>>();
		}

		String actionName = methodController.value();
		if ("".equals(actionName)) {
			actionName = method.getName();
		}

		scw.core.net.http.Method[] types = MethodAction.mergeRequestType(clz, method);
		for (scw.core.net.http.Method type : types) {
			Map<String, Action> map = clzMap.get(type.name());
			if (map == null) {
				map = new HashMap<String, Action>();
			}

			if (map.containsKey(actionName)) {
				throw new AlreadyExistsException(
						ServletPathServiceFilter.getExistActionErrMsg(action, map.get(actionName)));
			}

			map.put(actionName, action);
			clzMap.put(type.name(), map);
			actionMap.put(path, clzMap);
		}
	}
}
