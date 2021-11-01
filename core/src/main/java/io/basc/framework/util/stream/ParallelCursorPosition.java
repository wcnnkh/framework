package io.basc.framework.util.stream;

import java.util.concurrent.atomic.LongAdder;

public class ParallelCursorPosition implements CursorPosition {
	private final LongAdder position;

	public ParallelCursorPosition() {
		this(0);
	}

	public ParallelCursorPosition(long posotion) {
		this.position = new LongAdder();
		if (posotion != 0) {
			this.position.add(posotion);
		}
	}

	@Override
	public long getPosition() {
		return position.longValue();
	}

	@Override
	public void increment() {
		position.increment();
	}

	@Override
	public void decrement() {
		position.decrement();
	}

}
