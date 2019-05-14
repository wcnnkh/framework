package scw.core;

import java.io.Serializable;

import scw.core.utils.StringUtils;

public final class QueryString extends LinkedMultiValueMap<String, String> implements Serializable {
	private static final long serialVersionUID = 1L;

	private String query;

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

					add(k, v);
				}
			}
		}
	}

	@Override
	public String toString() {
		return query;
	}
}
