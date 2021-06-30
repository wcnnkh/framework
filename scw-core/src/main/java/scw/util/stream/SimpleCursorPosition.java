package scw.util.stream;

public final class SimpleCursorPosition implements CursorPosition {
	private long position;

	public SimpleCursorPosition(long position) {
		this.position = position;
	}

	@Override
	public long getPosition() {
		return position;
	}

	@Override
	public void increment() {
		position++;
	}

	@Override
	public void decrement() {
		position--;
	}

}
