package scw.mvc.action;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import scw.core.instance.annotation.Configuration;
import scw.http.HttpMethod;
import scw.http.server.HttpControllerDescriptor;
import scw.lang.AlreadyExistsException;
import scw.mvc.HttpChannel;
import scw.mvc.MVCUtils;

@Configuration(order=Integer.MIN_VALUE + 2)
public class PathActionLookup implements ActionLookup {
	private final Map<String, EnumMap<HttpMethod, Action>> actionMap = new HashMap<String, EnumMap<HttpMethod, Action>>();

	public PathActionLookup(ActionManager actionManager){
		for(Action action : actionManager.getActions()){
			register(action);
		}
	}
	
	public Action lookup(HttpChannel httpChannel) {
		Map<HttpMethod, Action> map = actionMap.get(httpChannel
				.getRequest().getPath());
		if (map == null) {
			return null;
		}

		return map.get(httpChannel.getRequest().getMethod());
	}

	public void register(Action action) {
		for (HttpControllerDescriptor descriptor : action
				.getHttpControllerDescriptors()) {
			if (descriptor.getRestful().isRestful()) {
				continue;
			}

			EnumMap<HttpMethod, Action> map = actionMap.get(descriptor
					.getPath());
			if (map == null) {
				map = new EnumMap<HttpMethod, Action>(HttpMethod.class);
			}

			if (map.containsKey(descriptor.getMethod())) {
				throw new AlreadyExistsException(MVCUtils.getExistActionErrMsg(
						action, map.get(descriptor.getMethod())));
			}
			map.put(descriptor.getMethod(), action);
			actionMap.put(descriptor.getPath(), map);
		}
	}
}
