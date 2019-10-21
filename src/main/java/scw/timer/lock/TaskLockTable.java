package scw.timer.lock;

import java.io.Serializable;

import scw.sql.orm.annotation.PrimaryKey;
import scw.sql.orm.annotation.Table;

@Table
public class TaskLockTable implements Serializable {
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private String taskId;
	private boolean cancel;
	private long lastTime;// 最后一次执行时间

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public boolean isCancel() {
		return cancel;
	}

	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}
}
