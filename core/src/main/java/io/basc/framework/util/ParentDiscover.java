package io.basc.framework.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
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
	default Enumeration<T> parents() {
		Iterator<T> iterator = new ParentIterator<>(ParentDiscover.this);
		if (!iterator.hasNext()) {
			return Collections.emptyEnumeration();
		}

		List<T> list = new LinkedList<T>();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return CollectionUtils.toEnumeration(CollectionUtils.getIterator(list, true));
	}
}
