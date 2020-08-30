package scw.mvc.action;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import scw.core.instance.annotation.Configuration;
import scw.http.HttpMethod;
import scw.http.HttpUtils;
import scw.http.server.HttpControllerDescriptor;
import scw.http.server.ServerHttpRequest;
import scw.lang.AlreadyExistsException;
import scw.mvc.MVCUtils;
import scw.value.Value;

@Configuration(order = Integer.MIN_VALUE + 1)
public class ParameterActionLookup implements ActionLookup {
	private final Map<String, EnumMap<HttpMethod, Map<String, Action>>> actionMap = new HashMap<String, EnumMap<HttpMethod, Map<String, Action>>>();
	private String key = "action";

	public ParameterActionLookup(ActionManager actionManager) {
		for (Action action : actionManager.getActions()) {
			register(action);
		}
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Action lookup(ServerHttpRequest request) {
		if (key == null) {
			return null;
		}

		Map<HttpMethod, Map<String, Action>> map = actionMap.get(request.getPath());
		if (map == null) {
			return null;
		}

		Map<String, Action> methodMap = map.get(request.getMethod());
		if (methodMap == null) {
			return null;
		}

		Value action = HttpUtils.getParameter(request, key);
		if (action == null || action.isEmpty()) {
			return null;
		}
		return methodMap.get(action.getAsString());
	}

	protected final void register(HttpMethod httpMethod, String classController, String methodController,
			Action action) {
		EnumMap<HttpMethod, Map<String, Action>> clzMap = actionMap.get(classController);
		if (clzMap == null) {
			clzMap = new EnumMap<HttpMethod, Map<String, Action>>(HttpMethod.class);
		}

		Map<String, Action> map = clzMap.get(httpMethod);
		if (map == null) {
			map = new HashMap<String, Action>();
		}

		if (map.containsKey(methodController)) {
			throw new AlreadyExistsException(MVCUtils.getExistActionErrMsg(action, map.get(methodController)));
		}

		map.put(methodController, action);
		clzMap.put(httpMethod, map);
		actionMap.put(classController, clzMap);
	}

	private Set<String> toControllerSet(Collection<HttpControllerDescriptor> httpHttpControllerDescriptors) {
		HashSet<String> actions = new HashSet<String>();
		for (HttpControllerDescriptor methodControllerDescriptor : httpHttpControllerDescriptors) {
			if (methodControllerDescriptor.getRestful().isRestful()) {
				continue;
			}

			actions.add(methodControllerDescriptor.getPath());
		}
		return actions;
	}

	public void register(Action action) {
		for (String classController : toControllerSet(action.getSourceClassHttpControllerDescriptors())) {
			for (String methodController : toControllerSet(action.getMethodHttpControllerDescriptors())) {
				for (HttpControllerDescriptor descriptor : action.getHttpControllerDescriptors()) {
					register(descriptor.getMethod(), classController, methodController, action);
				}
			}
		}
	}
}
