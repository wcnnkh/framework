package shuchaowen.web.admin.bean;

import java.io.Serializable;

import shuchaowen.core.db.annoation.PrimaryKey;

public class GroupAction implements Serializable{
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private long id;
	@PrimaryKey
	private long actionId;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getActionId() {
		return actionId;
	}
	public void setActionId(long actionId) {
		this.actionId = actionId;
	}
}
