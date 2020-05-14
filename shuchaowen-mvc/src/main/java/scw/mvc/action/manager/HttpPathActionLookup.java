package scw.mvc.action.manager;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import scw.beans.annotation.Bean;
import scw.core.instance.annotation.Configuration;
import scw.lang.AlreadyExistsException;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;
import scw.mvc.action.Action;
import scw.mvc.action.Action.ControllerDescriptor;
import scw.net.http.HttpMethod;

@Configuration(order=Integer.MIN_VALUE + 2)
@Bean(proxy=false)
public class HttpPathActionLookup implements ActionLookup {
	private final Map<String, EnumMap<HttpMethod, Action>> actionMap = new HashMap<String, EnumMap<HttpMethod, Action>>();

	public Action lookup(Channel channel) {
		Map<HttpMethod, Action> map = actionMap.get(channel
				.getRequest().getController());
		if (map == null) {
			return null;
		}

		return map.get(channel.getRequest().getMethod());
	}

	public void register(Action action) {
		for (ControllerDescriptor descriptor : action
				.getControllerDescriptors()) {
			if (descriptor.getRestful().isRestful()) {
				continue;
			}

			EnumMap<HttpMethod, Action> map = actionMap.get(descriptor
					.getController());
			if (map == null) {
				map = new EnumMap<HttpMethod, Action>(HttpMethod.class);
			}

			if (map.containsKey(descriptor.getHttpMethod())) {
				throw new AlreadyExistsException(MVCUtils.getExistActionErrMsg(
						action, map.get(descriptor.getHttpMethod())));
			}
			map.put(descriptor.getHttpMethod(), action);
			actionMap.put(descriptor.getController(), map);
		}
	}
}
