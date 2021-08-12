package scw.util.page;

public class JumpPageables<P extends Pageable<K, T>, PS extends Pageables<K, T>, K, T>
		extends PageableWrapper<P, K, T> implements Pageables<K, T> {
	protected final PS pageables;

	public JumpPageables(P pageable, PS pageables) {
		super(pageable);
		this.pageables = pageables;
	}

	@Override
	public Pageables<K, T> jumpTo(K cursorId) {
		return pageables.jumpTo(cursorId);
	}
}
