package scw.mvc.http.filter;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import scw.core.exception.AlreadyExistsException;
import scw.core.utils.XUtils;
import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;
import scw.mvc.annotation.Controller;
import scw.mvc.http.HttpRequest;

public final class ServletPathServiceFilter extends AbstractHttpServiceFilter {
	private final Map<String, EnumMap<scw.net.http.Method, Action<Channel>>> actionMap = new HashMap<String, EnumMap<scw.net.http.Method, Action<Channel>>>();

	public Action<Channel> getAction(HttpRequest request) {
		EnumMap<scw.net.http.Method, Action<Channel>> map = actionMap.get(request.getRequestPath());
		if (map == null) {
			return null;
		}

		scw.net.http.Method method = scw.net.http.Method.valueOf(request.getMethod());
		return map.get(method);
	}

	@Override
	public void scanning(Class<?> clz, Method method, Controller classController, Controller methodController,
			Action<Channel> action) {
		RestInfo restInfo = getRestInfo(action, clz, method);
		if (restInfo == null) {
			return;
		}

		if (restInfo.getKeyMap().size() > 0) {
			return;
		}

		String allPath = XUtils.mergePath("/", classController.value(), methodController.value());
		EnumMap<scw.net.http.Method, Action<Channel>> map = actionMap.get(allPath);
		if (map == null) {
			map = new EnumMap<scw.net.http.Method, Action<Channel>>(scw.net.http.Method.class);
		}

		scw.net.http.Method[] types = MVCUtils.mergeRequestType(clz, method);
		for (scw.net.http.Method type : types) {
			if (map.containsKey(type.name())) {
				throw new AlreadyExistsException(MVCUtils.getExistActionErrMsg(action, map.get(type.name())));
			}
			map.put(type, action);
			actionMap.put(allPath, map);
		}
	}
}
