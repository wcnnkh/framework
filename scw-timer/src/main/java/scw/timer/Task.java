package scw.timer;

public interface Task {
	void run(long executionTime) throws Throwable;
}
