package scw.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.utils.StringUtils;

public final class QueryString implements Serializable {
	private static final long serialVersionUID = 1L;

	private String query;
	private Map<String, LinkedList<String>> parameterMap;

	/**
	 * 用于序列化
	 */
	protected QueryString() {
	};

	public QueryString(String query) {
		this.query = query;
		if (!StringUtils.isEmpty(query)) {
			String[] arrs = query.split("&");
			if (arrs.length > 0) {
				parameterMap = new HashMap<String, LinkedList<String>>(arrs.length, 1);
				for (String arr : arrs) {
					if (StringUtils.isEmpty(arr)) {
						continue;
					}

					int index = arr.indexOf("=");
					if (index == -1) {
						continue;
					}

					String k = arr.substring(0, index);
					String v = "";
					index++;
					if (index < arr.length() - 1) {
						v = arr.substring(index);
					}

					LinkedList<String> list = parameterMap.get(k);
					if (list == null) {
						list = new LinkedList<String>();
						parameterMap.put(k, list);
					}

					list.add(v);
				}
			}
		}
	}

	public String getParameter(String name) {
		if (parameterMap == null) {
			return null;
		}

		LinkedList<String> list = parameterMap.get(name);
		if (list == null) {
			return null;
		}

		return list.getFirst();
	}

	public String[] getParameterValues(String name) {
		if (parameterMap == null) {
			return null;
		}

		LinkedList<String> list = parameterMap.get(name);
		if (list == null) {
			return null;
		}

		return list.toArray(new String[list.size()]);
	}

	@SuppressWarnings("unchecked")
	public Map<String, String[]> getParameterMap() {
		if (parameterMap == null) {
			return Collections.EMPTY_MAP;
		}

		Map<String, String[]> map = new HashMap<String, String[]>(parameterMap.size(), 1);
		for (Entry<String, LinkedList<String>> entry : parameterMap.entrySet()) {
			List<String> list = entry.getValue();
			map.put(entry.getKey(), list.toArray(new String[list.size()]));
		}
		return map;
	}

	public Enumeration<String> getParameterNames() {
		if (parameterMap == null) {
			return Collections.emptyEnumeration();
		}

		return Collections.enumeration(parameterMap.keySet());
	}

	@Override
	public String toString() {
		return query;
	}
}
