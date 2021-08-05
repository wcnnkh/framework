package scw.util.page;


public class JumpCursor<K, T> extends PageableWrapper<Pageable<K, T>, K, T>
		implements Cursor<K, T> {

	public JumpCursor(Pageable<K, T> pageable) {
		super(pageable);
	}

	@Override
	public void close() {
		if (wrappedTarget instanceof Cursor) {
			((Cursor<?, ?>) wrappedTarget).close();
		}
	}
}
