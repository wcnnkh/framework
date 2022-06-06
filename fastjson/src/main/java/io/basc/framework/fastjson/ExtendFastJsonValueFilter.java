package io.basc.framework.fastjson;

import com.alibaba.fastjson.serializer.ValueFilter;

import io.basc.framework.aop.support.FieldSetterListen;
import io.basc.framework.aop.support.FieldSetterListenImpl;
import io.basc.framework.aop.support.ProxyUtils;
import io.basc.framework.json.JSONAware;
import io.basc.framework.mapper.Copy;
import io.basc.framework.value.Value;

public class ExtendFastJsonValueFilter implements ValueFilter {
	public static final ValueFilter INSTANCE = new ExtendFastJsonValueFilter();

	private ExtendFastJsonValueFilter() {
	};

	public Object process(Object object, String name, Object value) {
		if (value == null) {
			return value;
		}

		if (value instanceof JSONAware) {
			return ((JSONAware) value).toJSONString();
		}

		if (value instanceof Value) {
			return ((Value) value).get();
		}

		// 这是应该还想办法屏蔽Gson的Factory对象
		if ("callbacks".equals(name)) {
			return null;
		}

		if (object instanceof FieldSetterListen && FieldSetterListenImpl.FIELD_SETTER_MAP_FIELD_NAME.equals(name)) {
			return null;
		}

		if (ProxyUtils.getFactory().isProxy(value.getClass())) {
			return Copy.copy(value, ProxyUtils.getFactory().getUserClass(value.getClass()));
		}
		return value;
	}
}
