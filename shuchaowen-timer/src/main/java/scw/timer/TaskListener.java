package scw.timer;

public interface TaskListener {
	void begin(TaskConfig config, long executionTime);

	void success(TaskConfig config, long executionTime);

	void error(TaskConfig config, long executionTime, Throwable e);

	void complete(TaskConfig config, long executionTime);
}
