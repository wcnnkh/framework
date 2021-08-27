package io.basc.framework.util.stream;

public interface CursorPosition extends StreamPosition {
	void increment();

	void decrement();
}
