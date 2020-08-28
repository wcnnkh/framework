package scw.timer.db;

import java.io.Serializable;

import scw.sql.orm.annotation.PrimaryKey;
import scw.sql.orm.annotation.Table;

@Table
public class TaskLockTable implements Serializable {
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private String taskId;
	private long lastTime;// 最后一次执行时间

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}
}
