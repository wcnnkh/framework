package scw.util.page;

import scw.event.ObjectEvent;

public class PageableEvent<K, T> extends ObjectEvent<Pageable<K, T>> {
	private static final long serialVersionUID = 1L;

	public PageableEvent(Pageable<K, T> source) {
		super(source);
	}
}
