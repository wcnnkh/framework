package scw.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import scw.common.utils.CollectionUtils;
import scw.common.utils.StringUtils;

public final class ServletUtils {
	private ServletUtils() {
	}

	public static List<Map<String, String>> getParameterMapList(ServletRequest servletRequest,
			Collection<String> keys) {
		if (CollectionUtils.isEmpty(keys)) {
			return null;
		}

		int size = 0;
		for (String key : keys) {
			if (StringUtils.isEmpty(key)) {
				continue;
			}

			String[] values = servletRequest.getParameterValues(key);
			if (values == null) {
				continue;
			}

			size = values.length;
			break;
		}

		List<Map<String, String>> list = new ArrayList<Map<String, String>>(size);
		for (int i = 0; i < size; i++) {
			list.add(getParameterMap(servletRequest, keys, i));
		}
		return list;
	}

	private static Map<String, String> getParameterMap(ServletRequest servletRequest, Collection<String> keys,
			int index) {
		Map<String, String> map = new HashMap<String, String>(keys.size(), 1F);
		for (String key : keys) {
			if (StringUtils.isEmpty(key)) {
				continue;
			}

			String[] values = servletRequest.getParameterValues(key);
			if (values == null) {
				continue;
			}

			map.put(key, values[index]);
		}
		return map;
	}
}
