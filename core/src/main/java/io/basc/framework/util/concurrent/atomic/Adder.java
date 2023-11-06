package io.basc.framework.util.concurrent.atomic;

/**
 * 加法器
 */
public interface Adder {
	void add(Number number);

	Number sum();

	void reset();
}
