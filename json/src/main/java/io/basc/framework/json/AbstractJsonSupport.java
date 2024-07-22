package io.basc.framework.json;

import io.basc.framework.util.ClassUtils;

public abstract class AbstractJsonSupport implements JsonSupport {

	public final String toJsonString(Object obj) {
		if (obj == null) {
			return null;
		}

		if (obj instanceof String) {
			return (String) obj;
		}

		if (ClassUtils.isPrimitiveOrWrapper(obj.getClass())) {
			return String.valueOf(obj);
		}

		if (obj instanceof JsonAware) {
			return ((JsonAware) obj).toJsonString();
		}

		return toJsonStringInternal(obj);
	}

	protected abstract String toJsonStringInternal(Object obj);
}
