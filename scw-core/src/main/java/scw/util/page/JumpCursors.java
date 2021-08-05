package scw.util.page;

public class JumpCursors<K, T> extends JumpPageables<K, T> implements
		Cursors<K, T> {

	public JumpCursors(Pageable<K, T> pageable, PageableProcessor<K, T> processor) {
		super(pageable, processor);
	}

	@Override
	public Cursors<K, T> process(K start, long count) {
		Pageable<K, T> pageable = super.process(start, count);
		return new JumpCursors<>(pageable, this);
	}

	@Override
	public void close() {
		if (wrappedTarget instanceof Cursors) {
			((Cursors<?, ?>) wrappedTarget).close();
		}
	}
}
