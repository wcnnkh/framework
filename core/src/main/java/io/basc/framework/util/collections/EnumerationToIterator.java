package io.basc.framework.util.collections;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;

import io.basc.framework.util.Assert;
import lombok.Data;

/**
 * 枚举转迭代
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
@Data
public class EnumerationToIterator<S, E> implements Iterator<E>, Serializable {
	private static final long serialVersionUID = 1L;
	private final Enumeration<? extends S> enumeration;
	private final Function<? super S, ? extends E> converter;

	public EnumerationToIterator(Enumeration<? extends S> enumeration, Function<? super S, ? extends E> converter) {
		Assert.requiredArgument(enumeration != null, "enumeration");
		Assert.requiredArgument(converter != null, "converter");
		this.enumeration = enumeration;
		this.converter = converter;
	}

	@Override
	public boolean hasNext() {
		return enumeration.hasMoreElements();
	}

	@Override
	public E next() {
		S e = enumeration.nextElement();
		return converter.apply(e);
	}

}
