package scw.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class AcceptIterator<E> extends AbstractIterator<E> {
	private final Iterator<E> iterator;
	private final Accept<E> accept;
	private Supplier<E> supplier;

	public AcceptIterator(Iterator<E> iterator, Accept<E> accept) {
		this.iterator = iterator;
		this.accept = accept;
	}

	public boolean hasNext() {
		if(accept == null){
			return iterator.hasNext();
		}
		
		if (supplier != null) {
			return true;
		}

		while (iterator.hasNext()) {
			E item = iterator.next();
			if (accept.accept(item)) {
				supplier = new StaticSupplier<E>(item);
				return true;
			}
		}
		return false;
	}

	public E next() {
		if(accept == null){
			return iterator.next();
		}
		
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		E item = supplier.get();
		supplier = null;
		return item;
	}
}
