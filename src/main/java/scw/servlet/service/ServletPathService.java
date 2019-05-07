package scw.servlet.service;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.beans.annotation.Bean;
import scw.core.exception.AlreadyExistsException;
import scw.core.utils.XUtils;
import scw.servlet.Request;
import scw.servlet.annotation.Controller;

@Bean(proxy=false)
public class ServletPathService extends AbstractServiceFilter {
	private final Map<String, EnumMap<scw.core.net.http.Method, Action>> actionMap = new HashMap<String, EnumMap<scw.core.net.http.Method, Action>>();
	private BeanFactory beanFactory;

	public ServletPathService(BeanFactory beanFactory, Collection<Class<?>> classes) {
		super(classes);
		this.beanFactory = beanFactory;
	}
	
	@Override
	public void init() {
		super.init();
		this.beanFactory = null;
	}

	public Action getAction(Request request) {
		EnumMap<scw.core.net.http.Method, Action> map = actionMap.get(request.getServletPath());
		if (map == null) {
			return null;
		}

		scw.core.net.http.Method method = scw.core.net.http.Method.valueOf(request.getMethod());
		return map.get(method);
	}

	@Override
	public void scanning(Class<?> clz, Method method, Controller classController, Controller methodController) {
		RestInfo restInfo = getRestInfo(beanFactory, clz, method);
		if (restInfo == null) {
			return;
		}

		if (restInfo.getKeyMap().size() > 0) {
			return;
		}

		String allPath = XUtils.mergePath("/", classController.value(), methodController.value());
		Action action = restInfo.getAction();
		EnumMap<scw.core.net.http.Method, Action> map = actionMap.get(allPath);
		if (map == null) {
			map = new EnumMap<scw.core.net.http.Method, Action>(scw.core.net.http.Method.class);
		}

		scw.core.net.http.Method[] types = MethodAction.mergeRequestType(clz, method);
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
