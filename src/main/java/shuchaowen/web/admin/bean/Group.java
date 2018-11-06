package shuchaowen.web.admin.bean;

import java.io.Serializable;

import shuchaowen.core.db.annoation.PrimaryKey;

public class Group implements Serializable{
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private long id;
	private String name;
	private int level;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
}
