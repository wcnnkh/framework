package scw.servlet.service;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.beans.annotation.Bean;
import scw.common.exception.AlreadyExistsException;
import scw.common.utils.XUtils;
import scw.servlet.Request;
import scw.servlet.annotation.Controller;

@Bean(proxy=false)
public class ParameterActionService extends AbstractService {
	private final Map<String, Map<String, Map<String, Action>>> actionMap = new HashMap<String, Map<String, Map<String, Action>>>();
	private String key;
	private BeanFactory beanFactory;

	public ParameterActionService(BeanFactory beanFactory, Collection<Class<?>> classes, String key) {
		super(classes);
		this.beanFactory = beanFactory;
		this.key = key;
	}

	@Override
	public Action getAction(Request request) {
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

		String action = request.getString(key);
		if (action == null) {
			return null;
		}
		return methodMap.get(action);
	}

	@Override
	public CrossDomainDefinition getCrossDomainDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void scanning(Class<?> clz, Method method, Controller classController, Controller methodController) {
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

		Action action = new MethodAction(beanFactory, clz, method);
		scw.net.http.enums.Method[] types = MethodAction.mergeRequestType(clz, method);
		for (scw.net.http.enums.Method type : types) {
			Map<String, Action> map = clzMap.get(type.name());
			if (map == null) {
				map = new HashMap<String, Action>();
			}

			if (map.containsKey(actionName)) {
				throw new AlreadyExistsException(ServletPathService.getExistActionErrMsg(action, map.get(actionName)));
			}

			map.put(actionName, action);
			clzMap.put(type.name(), map);
			actionMap.put(path, clzMap);
		}
	}
}
