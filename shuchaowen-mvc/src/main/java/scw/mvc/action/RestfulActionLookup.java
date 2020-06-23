package scw.mvc.action;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.instance.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.http.HttpMethod;
import scw.http.server.HttpControllerDescriptor;
import scw.http.server.ServerHttpRequest;
import scw.lang.AlreadyExistsException;
import scw.mvc.HttpChannel;
import scw.mvc.MVCUtils;
import scw.net.Restful;
import scw.net.Restful.RestfulMatchingResult;
import scw.net.RestfulParameterMapAware;

@Configuration(order = Integer.MIN_VALUE)
public class RestfulActionLookup implements ActionLookup {
	private final EnumMap<HttpMethod, Map<Restful, Action>> restMap = new EnumMap<HttpMethod, Map<Restful, Action>>(
			HttpMethod.class);

	public RestfulActionLookup(ActionManager actionManager) {
		for (Action action : actionManager.getActions()) {
			register(action);
		}
	}

	public void register(Action action) {
		for (HttpControllerDescriptor descriptor : action.getHttpControllerDescriptors()) {
			if (!descriptor.getRestful().isRestful()) {
				continue;
			}

			Map<Restful, Action> map = restMap.get(descriptor.getMethod());
			if (map == null) {
				map = new HashMap<Restful, Action>();
			}

			if (map.containsKey(descriptor.getRestful())) {
				throw new AlreadyExistsException(
						MVCUtils.getExistActionErrMsg(action, map.get(descriptor.getRestful())));
			}

			map.put(descriptor.getRestful(), action);
			restMap.put(descriptor.getMethod(), map);
		}
	}

	public Action lookup(HttpChannel httpChannel) {
		Map<Restful, Action> map = restMap.get(httpChannel.getRequest().getMethod());
		if (map == null) {
			return null;
		}

		String[] pathArr = StringUtils.split(httpChannel.getRequest().getPath(), '/');
		for (Entry<Restful, Action> entry : map.entrySet()) {
			Restful restful = entry.getKey();
			RestfulMatchingResult result = restful.matching(pathArr);
			if (result.isSuccess()) {
				ServerHttpRequest request = httpChannel.getRequest();
				if(request instanceof RestfulParameterMapAware){
					((RestfulParameterMapAware) request).setRestfulParameterMap(result.getParameterMap());
				}
				return entry.getValue();
			}
		}
		return null;
	}
}
