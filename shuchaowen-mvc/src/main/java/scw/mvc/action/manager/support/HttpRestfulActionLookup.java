package scw.mvc.action.manager.support;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.instance.annotation.Configuration;
import scw.lang.AlreadyExistsException;
import scw.mvc.MVCUtils;
import scw.mvc.action.manager.HttpAction;
import scw.mvc.action.manager.HttpAction.ControllerDescriptor;
import scw.mvc.action.manager.HttpActionLookup;
import scw.mvc.http.HttpChannel;
import scw.net.Restful;
import scw.net.Restful.RestfulMatchingResult;
import scw.net.http.HttpMethod;

@Configuration(order=Integer.MIN_VALUE)
public final class HttpRestfulActionLookup extends HttpActionLookup {
	private final EnumMap<HttpMethod, Map<Restful, HttpAction>> restMap = new EnumMap<HttpMethod, Map<Restful, HttpAction>>(
			HttpMethod.class);

	@Override
	public void register(HttpAction action) {
		for (ControllerDescriptor descriptor : action
				.getControllerDescriptors()) {
			if (!descriptor.getRestful().isRestful()) {
				continue;
			}

			Map<Restful, HttpAction> map = restMap.get(descriptor
					.getHttpMethod());
			if (map == null) {
				map = new HashMap<Restful, HttpAction>();
			}

			if (map.containsKey(descriptor.getRestful())) {
				throw new AlreadyExistsException(MVCUtils.getExistActionErrMsg(
						action, map.get(descriptor.getRestful())));
			}

			map.put(descriptor.getRestful(), action);
			restMap.put(descriptor.getHttpMethod(), map);
		}
	}

	@Override
	public HttpAction lookup(HttpChannel channel) {
		Map<Restful, HttpAction> map = restMap.get(channel.getRequest()
				.getMethod());
		if (map == null) {
			return null;
		}

		String[] pathArr = channel.getRequest().getControllerPath().split("/");
		for (Entry<Restful, HttpAction> entry : map.entrySet()) {
			Restful restful = entry.getKey();
			RestfulMatchingResult result = restful.matching(pathArr);
			if (result.isSuccess()) {
				MVCUtils.setRestfulParameterMap(channel,
						result.getParameterMap());
				return entry.getValue();
			}
		}
		return null;
	}
}
