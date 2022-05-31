package io.basc.framework.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public interface ParentDiscover<T extends ParentDiscover<T>> {
	T getParent();

	default boolean hasParent() {
		return getParent() != null;
	}

	/**
	 * 获取所有的父级
	 * 
	 * @return
	 */
	default List<T> getParents() {
		Iterator<T> iterator = new ParentIterator<>(ParentDiscover.this);
		return CollectionUtils.reversal(Collections.list(CollectionUtils.toEnumeration(iterator)));
	}
}
