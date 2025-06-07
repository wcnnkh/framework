package run.soeasy.framework.core.page;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PageIterator<E> implements Iterator<List<E>> {
	@NonNull
	private final Iterator<? extends E> iterator;
	private final int pageSize;
	private Supplier<List<E>> listSupplier;
	@Getter
	private int page = 0;

	@Override
	public synchronized boolean hasNext() {
		if (listSupplier == null && iterator.hasNext()) {
			page++;
			List<E> list = new ArrayList<>(pageSize);
			while (iterator.hasNext() && list.size() < pageSize) {
				list.add(iterator.next());
			}
			listSupplier = () -> list;
		}
		return listSupplier != null;
	}

	@Override
	public synchronized List<E> next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		try {
			return listSupplier.get();
		} finally {
			listSupplier = null;
		}
	}

}
