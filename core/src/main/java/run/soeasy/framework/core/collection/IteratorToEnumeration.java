package run.soeasy.framework.core.collection;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 迭代转枚举
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
@RequiredArgsConstructor
class IteratorToEnumeration<S, E> implements Enumeration<E>, Serializable {
	private static final long serialVersionUID = 1L;
	@NonNull
	private final Iterator<? extends S> iterator;
	@NonNull
	private final Function<? super S, ? extends E> converter;

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
