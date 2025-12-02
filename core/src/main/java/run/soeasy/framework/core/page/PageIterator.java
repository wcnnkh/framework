package run.soeasy.framework.core.page;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Listable;

@RequiredArgsConstructor
public class PageIterator<E> implements Iterator<Pageable<Long, E>> {
	@Getter
	private final int pageSize;
	@NonNull
	private final Iterator<? extends E> iterator;
	private volatile int page = 0;
	private volatile Supplier<Pageable<Long, E>> supplier;

	@Override
	public synchronized boolean hasNext() {
		if (supplier == null && iterator.hasNext()) {
			List<E> list = new ArrayList<>(pageSize);
			while (iterator.hasNext() && list.size() < pageSize) {
				list.add(iterator.next());
			}

			long offset = page * pageSize;
			Pageable<Long, E> pageable = new Cursor<Long, E>(offset, Listable.forCollection(list), offset + pageSize,
					null);
			supplier = () -> pageable;
		}
		return supplier != null;
	}

	@Override
	public synchronized Pageable<Long, E> next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		try {
			return supplier.get();
		} finally {
			supplier = null;
		}
	}

}
