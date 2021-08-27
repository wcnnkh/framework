package io.basc.framework.timer;

public interface Task {
	void run(long executionTime) throws Throwable;
}
