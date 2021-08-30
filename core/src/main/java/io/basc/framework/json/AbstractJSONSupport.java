package io.basc.framework.json;

import io.basc.framework.util.ClassUtils;

public abstract class AbstractJSONSupport implements JSONSupport {

	public final String toJSONString(Object obj) {
		if (obj == null) {
			return null;
		}

		if (obj instanceof String) {
			return (String) obj;
		}

		if (ClassUtils.isPrimitiveOrWrapper(obj.getClass())) {
			return String.valueOf(obj);
		}

		if (obj instanceof JSONAware) {
			return ((JSONAware) obj).toJSONString();
		}

		return toJsonStringInternal(obj);
	}

	protected abstract String toJsonStringInternal(Object obj);
}
