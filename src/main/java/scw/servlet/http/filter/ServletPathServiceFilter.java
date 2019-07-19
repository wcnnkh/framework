package scw.servlet.http.filter;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import scw.core.exception.AlreadyExistsException;
import scw.core.utils.XUtils;
import scw.servlet.Action;
import scw.servlet.MethodAction;
import scw.servlet.annotation.Controller;

final class ServletPathServiceFilter extends AbstractHttpServiceFilter {
	private final Map<String, EnumMap<scw.net.http.Method, Action>> actionMap = new HashMap<String, EnumMap<scw.net.http.Method, Action>>();

	public Action getAction(HttpServletRequest request) {
		EnumMap<scw.net.http.Method, Action> map = actionMap.get(request.getServletPath());
		if (map == null) {
			return null;
		}

		scw.net.http.Method method = scw.net.http.Method.valueOf(request.getMethod());
		return map.get(method);
	}

	@Override
	public void scanning(Class<?> clz, Method method, Controller classController, Controller methodController,
			Action action) {
		RestInfo restInfo = getRestInfo(action, clz, method);
		if (restInfo == null) {
			return;
		}

		if (restInfo.getKeyMap().size() > 0) {
			return;
		}

		String allPath = XUtils.mergePath("/", classController.value(), methodController.value());
		EnumMap<scw.net.http.Method, Action> map = actionMap.get(allPath);
		if (map == null) {
			map = new EnumMap<scw.net.http.Method, Action>(scw.net.http.Method.class);
		}

		scw.net.http.Method[] types = MethodAction.mergeRequestType(clz, method);
		for (scw.net.http.Method type : types) {
			if (map.containsKey(type.name())) {
				throw new AlreadyExistsException(getExistActionErrMsg(action, map.get(type.name())));
			}
			map.put(type, action);
			actionMap.put(allPath, map);
		}
	}

	public static String getExistActionErrMsg(Action action, Action oldAction) {
		StringBuilder sb = new StringBuilder();
		sb.append("存在同样的controller[");
		sb.append(action.toString());
		sb.append("],原来的[");
		sb.append(oldAction.toString());
		sb.append("]");
		return sb.toString();
	}
}
