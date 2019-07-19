package scw.json.support.fastjson;

import com.alibaba.fastjson.serializer.PropertyFilter;

import scw.cglib.proxy.Factory;

public class BaseProperyFilter implements PropertyFilter {
	public static final BaseProperyFilter BASE_PROPERY_FILTER = new BaseProperyFilter();

	private BaseProperyFilter() {
	};

	public boolean apply(Object object, String name, Object value) {
		if (object instanceof Factory && "callbacks".equals(name)) {
			return false;
		}
		return true;
	}

}
