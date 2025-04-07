package run.soeasy.framework.util.collection;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;

import lombok.Data;
import run.soeasy.framework.util.Assert;

/**
 * 迭代转枚举
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
@Data
public class IteratorToEnumeration<S, E> implements Enumeration<E>, Serializable {
	private static final long serialVersionUID = 1L;
	private final Iterator<? extends S> iterator;
	private final Function<? super S, ? extends E> converter;

	public IteratorToEnumeration(Iterator<? extends S> iterator, Function<? super S, ? extends E> converter) {
		Assert.requiredArgument(iterator != null, "iterator");
		Assert.requiredArgument(converter != null, "converter");
		this.iterator = iterator;
		this.converter = converter;
	}

	@Override
	public boolean hasMoreElements() {
		return iterator.hasNext();
	}

	@Override
	public E nextElement() {
		S s = iterator.next();
		return converter.apply(s);
	}

}
