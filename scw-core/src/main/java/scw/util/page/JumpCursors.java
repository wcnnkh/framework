package scw.util.page;

public class JumpCursors<K, T> extends JumpPageables<K, T> implements Cursors<K, T> {

	public JumpCursors(PageableProcessor<K, T> processor, Pageable<K, T> pageable) {
		super(processor, pageable);
	}

	@Override
	public boolean isClosed() {
		if (wrappedTarget instanceof Cursors) {
			return ((Cursors<?, ?>) wrappedTarget).isClosed();
		}
		return false;
	}

	@Override
	public void close() {
		if (wrappedTarget instanceof Cursors) {
			((Cursors<?, ?>) wrappedTarget).close();
		}
	}

	@Override
	public Cursors<K, T> next(PageableProcessor<K, T> processor) {
		return Cursors.super.next(processor);
	}
	
	@Override
	public Cursors<K, T> jumpTo(PageableProcessor<K, T> processor, K cursorId) {
		return Cursors.super.jumpTo(processor, cursorId);
	}
}
