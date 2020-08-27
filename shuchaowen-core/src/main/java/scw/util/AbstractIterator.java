package scw.util;

public abstract class AbstractIterator<E> implements java.util.Iterator<E> {

	public void remove() {
		throw new UnsupportedOperationException("remove");
	}

}
