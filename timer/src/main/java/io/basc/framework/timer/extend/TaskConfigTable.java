package io.basc.framework.timer.extend;

import io.basc.framework.sql.orm.annotation.Table;

import java.io.Serializable;

@Table
public class TaskConfigTable implements Serializable {
	private static final long serialVersionUID = 1L;
	private String taskId;
	private int type;
	private String config;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}
}
