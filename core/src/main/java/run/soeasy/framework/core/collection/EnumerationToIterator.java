package run.soeasy.framework.core.collection;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 枚举转迭代
 * 
 * @author soeasy.run
 *
 * @param <E>
 */
@RequiredArgsConstructor
class EnumerationToIterator<S, E> implements Iterator<E>, Serializable {
	private static final long serialVersionUID = 1L;
	@NonNull
	private final Enumeration<? extends S> enumeration;
	@NonNull
	private final Function<? super S, ? extends E> converter;

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
