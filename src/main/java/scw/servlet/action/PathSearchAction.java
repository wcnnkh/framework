package scw.servlet.action;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.common.exception.AlreadyExistsException;
import scw.common.utils.XUtils;
import scw.servlet.Request;
import scw.servlet.action.annotation.Controller;

public class PathSearchAction implements SearchAction {
	private final Map<String, Map<String, Action>> actionMap = new HashMap<String, Map<String, Action>>();
	private BeanFactory beanFactory;

	public PathSearchAction(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public Action getAction(Request request) throws Exception {
		Map<String, Action> map = actionMap.get(request.getServletPath());
		if (map == null) {
			return null;
		}
		return map.get(request.getMethod());
	}

	public void init(Collection<Class<?>> classList) throws Exception {
		for (Class<?> clz : classList) {
			Controller clzController = clz.getAnnotation(Controller.class);
			if (clzController == null) {
				continue;
			}

			for (Method method : clz.getDeclaredMethods()) {
				Deprecated d = method.getAnnotation(Deprecated.class);
				if (d != null) {
					continue;
				}

				Controller methodController = method.getAnnotation(Controller.class);
				if (methodController == null) {
					continue;
				}

				RestInfo restInfo = RestSearchAction.getRestInfo(beanFactory, clz, method);
				if (restInfo == null) {
					continue;
				}

				if (restInfo.getKeyMap().size() > 0) {
					continue;
				}

				String allPath = XUtils.mergePath("/", clzController.value(), methodController.value());
				Action action = restInfo.getAction();
				Map<String, Action> map = actionMap.get(allPath);
				if (map == null) {
					map = new HashMap<String, Action>();
				}

				scw.net.http.enums.Method[] types = MethodAction.mergeRequestType(clz, method);
				for (scw.net.http.enums.Method type : types) {
					if (map.containsKey(type.name())) {
						throw new AlreadyExistsException(getExistActionErrMsg(action, map.get(type.name())));
					}
					map.put(type.name(), action);
					actionMap.put(allPath, map);
				}
			}
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