package scw.timer;

public interface TaskConfig {
	String getTaskId();

	Task getTask();

	TaskListener getTaskListener();
	
	/**
	 * 是否是分布式任务
	 * @return
	 */
	boolean isDistributed();
}
