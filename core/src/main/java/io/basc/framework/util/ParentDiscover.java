package io.basc.framework.util;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.element.Elements;

public interface ParentDiscover<T extends ParentDiscover<T>> {
	@Nullable
	T getParent();

	default boolean hasParent() {
		return getParent() != null;
	}

	default Elements<T> parents() {
		return Elements.of(() -> new ParentIterator<>(this));
	}

	default boolean isParents(T parent) {
		if (parent == null || !hasParent()) {
			return false;
		}

		T p = getParent();
		while (true) {
			if (p == parent || parent.equals(p)) {
				return true;
			}

			if (!p.hasParent()) {
				return false;
			}

			p = p.getParent();
		}
	}
}
