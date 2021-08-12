package scw.util.page;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class PageStream<K, T> implements Iterator<T> {
	private Pageables<K, T> pageables;
	private Iterator<T> iterator;

	public PageStream(Pageables<K, T> pageables) {
		this.pageables = pageables;
		this.iterator = pageables.iterator();
	}

	public Pageables<K, T> getPageables() {
		return pageables;
	}

	@Override
	public boolean hasNext() {
		if (iterator.hasNext()) {
			return true;
		}

		if (pageables.hasNext()) {
			pageables = pageables.next();
			iterator = pageables.iterator();
			return hasNext();
		}
		return false;
	}

	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return iterator.next();
	}

}
