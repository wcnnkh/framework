package scw.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;

public final class MultiEnumeration<E> implements Enumeration<E> {
	private Iterator<Enumeration<E>> iterator;
	private Enumeration<? extends E> enumeration;
	
	public MultiEnumeration(Enumeration<E> ...enumerations) {
		this(Arrays.asList(enumerations));
	}

	public MultiEnumeration(Collection<Enumeration<E>> enumerations) {
		this.iterator = CollectionUtils.isEmpty(enumerations) ? null
				: enumerations.iterator();
	}

	public boolean hasMoreElements() {
		if (this.enumeration == null) {
			if (iterator.hasNext()) {
				this.enumeration = this.iterator.next();
			} else {
				this.enumeration = null;
			}
		} else if (!enumeration.hasMoreElements()) {
			if (iterator.hasNext()) {
				this.enumeration = this.iterator.next();
			} else {
				this.enumeration = null;
			}
		}
		
		if(enumeration == null){
			return false;
		}
		
		return enumeration.hasMoreElements();
	}

	public E nextElement() {
		if (enumeration == null) {
			throw new UnsupportedOperationException(
					"Call the hasnext method first");
		}
		
		return enumeration.nextElement();
	}

}
