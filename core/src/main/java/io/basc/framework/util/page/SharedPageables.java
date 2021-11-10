package io.basc.framework.util.page;

public class SharedPageables<K, T> extends SharedPageable<K, T> implements Pageables<K, T> {
	private static final long serialVersionUID = 1L;
	private final transient Pageables<K, T> pageables;

	public SharedPageables(Pageables<K, T> pageables) {
		super(pageables);
		this.pageables = pageables;
	}

	@Override
	public Pageables<K, T> jumpTo(K cursorId) {
		Pageables<K, T> pageables = this.pageables.jumpTo(cursorId);
		return new SharedPageables<>(pageables);
	}

	@Override
	public Pageables<K, T> shared() {
		return this;
	}
}
