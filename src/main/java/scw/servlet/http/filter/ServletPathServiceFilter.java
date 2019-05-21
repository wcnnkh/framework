package scw.servlet.http.filter;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import scw.core.exception.AlreadyExistsException;
import scw.core.utils.XUtils;
import scw.servlet.Action;
import scw.servlet.DefaultMethodAction;
import scw.servlet.annotation.Controller;

final class ServletPathServiceFilter extends AbstractHttpServiceFilter {
	private final Map<String, EnumMap<scw.core.net.http.Method, Action>> actionMap = new HashMap<String, EnumMap<scw.core.net.http.Method, Action>>();

	public Action getAction(HttpServletRequest request) {
		EnumMap<scw.core.net.http.Method, Action> map = actionMap.get(request.getServletPath());
		if (map == null) {
			return null;
		}

		scw.core.net.http.Method method = scw.core.net.http.Method.valueOf(request.getMethod());
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
		EnumMap<scw.core.net.http.Method, Action> map = actionMap.get(allPath);
		if (map == null) {
			map = new EnumMap<scw.core.net.http.Method, Action>(scw.core.net.http.Method.class);
		}

		scw.core.net.http.Method[] types = DefaultMethodAction.mergeRequestType(clz, method);
		for (scw.core.net.http.Method type : types) {
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
