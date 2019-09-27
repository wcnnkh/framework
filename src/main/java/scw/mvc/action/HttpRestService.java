package scw.mvc.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.exception.AlreadyExistsException;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpChannel;
import scw.net.http.Method;

public final class HttpRestService extends HttpActionService {
	private final Map<String, Map<String, HttpRestInfo>> restMap = new HashMap<String, Map<String, HttpRestInfo>>();

	public HttpRestService(Collection<ActionFilter> actionFilters) {
		super(actionFilters);
	}

	@Override
	public void scanning(HttpAction action, HttpControllerConfig config) {
		HttpRestInfo restUrlInfo = HttpRestInfo.getRestInfo(action, config);
		if (restUrlInfo == null) {
			return;
		}

		if (restUrlInfo.getKeyMap().size() == 0) {
			return;
		}

		/**
		 * resturl
		 */
		Map<String, HttpRestInfo> map = restMap.get(config.getMethod());
		if (map == null) {
			map = new HashMap<String, HttpRestInfo>();
		}

		if (map.containsKey(restUrlInfo.getUrl())) {
			throw new AlreadyExistsException(
					MVCUtils.getExistActionErrMsg(restUrlInfo.getAction(), map.get(restUrlInfo.getUrl()).getAction()));
		}

		map.put(restUrlInfo.getUrl(), restUrlInfo);
		restMap.put(config.getMethod(), map);
	}

	@Override
	public HttpAction getAction(HttpChannel channel) {
		Method method = Method.valueOf(channel.getRequest().getMethod());
		if (method == null) {
			return null;
		}

		Map<String, HttpRestInfo> map = restMap.get(method);
		if (map == null) {
			return null;
		}

		String[] pathArr = channel.getRequest().getRequestPath().split("/");
		for (Entry<String, HttpRestInfo> entry : map.entrySet()) {
			HttpRestInfo restUrl = entry.getValue();
			if (pathArr.length != restUrl.getRegArr().length) {
				continue;
			}

			Map<String, String> valueMap = null;
			boolean find = true;
			for (int i = 0; i < restUrl.getRegArr().length; i++) {
				String str = restUrl.getRegArr()[i];
				if ("*".equals(str)) {
					if (valueMap == null) {
						valueMap = new HashMap<String, String>();
					}

					String key = restUrl.getKeyMap().get(i);
					valueMap.put(key, pathArr[i]);
				} else if (!pathArr[i].equals(str)) {
					find = false;
					break;
				}
			}

			if (find) {
				MVCUtils.setRestPathParameterMap(channel, valueMap);
				return restUrl.getAction();
			}
		}
		return null;
	}
}
