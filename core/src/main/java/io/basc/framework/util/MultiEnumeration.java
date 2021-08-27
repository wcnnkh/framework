package io.basc.framework.util;

import io.basc.framework.core.utils.CollectionUtils;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

public final class MultiEnumeration<E> implements Enumeration<E> {
	private Iterator<Enumeration<E>> iterator;
	private Enumeration<? extends E> enumeration;

	public MultiEnumeration(Collection<Enumeration<E>> enumerations) {
		this.iterator = CollectionUtils.isEmpty(enumerations) ? null : enumerations.iterator();
	}

	public boolean hasMoreElements() {
		if(enumeration != null && enumeration.hasMoreElements()){
			return true;
		}
		
		while (iterator != null && iterator.hasNext()) {
			enumeration = iterator.next();
			if (enumeration != null && enumeration.hasMoreElements()) {
				return true;
			}

			enumeration = null;
		}

		return false;
	}

	public E nextElement() {
		if (enumeration == null) {
			throw new UnsupportedOperationException("Call the hasnext method first");
		}

		return enumeration.nextElement();
	}

}
