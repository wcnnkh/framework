package scw.util.page;

public class EmptyCursor<K, T> extends EmptyPageable<K, T> implements
		Cursor<K, T> {
	private static final long serialVersionUID = 1L;
	
	public EmptyCursor(K cursorId, Long count) {
		super(cursorId, count);
	}

	@Override
	public void close() {
	}
}
