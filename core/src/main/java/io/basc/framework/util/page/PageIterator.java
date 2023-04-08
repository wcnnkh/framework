package io.basc.framework.util.page;

public class PageIterator<K, T> extends PageableIterator<K, T> {

	public PageIterator(Pages<K, T> pageables) {
		super(pageables);
	}

	@Override
	public Page<K, T> next() {
		return (Page<K, T>) super.next();
	}
}
