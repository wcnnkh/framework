package scw.timer;

public interface TaskConfig {
	String getTaskId();

	Task getTask();

	TaskListener getTaskListener();
}
