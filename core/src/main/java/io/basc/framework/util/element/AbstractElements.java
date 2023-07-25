package io.basc.framework.util.element;

import io.basc.framework.util.ObjectUtils;

/**
 * 重写基础方法
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public abstract class AbstractElements<E> implements Elements<E> {
	@Override
	public String toString() {
		return toList().toString();
	}

	@Override
	public int hashCode() {
		return Elements.super.hashCode((e) -> e.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof Streamable) {
			Streamable<?> streamable = (Streamable<?>) obj;
			return equals(streamable, ObjectUtils::equals);
		}
		return false;
	}
}
