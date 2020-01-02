package scw.json.support;

import com.alibaba.fastjson.serializer.PropertyFilter;

import scw.core.cglib.proxy.Factory;

public final class FastJSONBaseProperyFilter implements PropertyFilter {
	public static final FastJSONBaseProperyFilter BASE_PROPERY_FILTER = new FastJSONBaseProperyFilter();

	private FastJSONBaseProperyFilter() {
	};

	public boolean apply(Object object, String name, Object value) {
		if (object instanceof Factory && "callbacks".equals(name)) {
			return false;
		}
		return true;
	}

}
