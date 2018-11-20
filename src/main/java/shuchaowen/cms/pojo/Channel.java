package shuchaowen.cms.pojo;

import java.io.Serializable;

import shuchaowen.core.db.annoation.PrimaryKey;
import shuchaowen.core.db.annoation.Table;

@Table
public class Channel implements Serializable{
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private long id;
	private long parentId;
	private String name;
	private int status;
	private long cts;
	private int weight;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getCts() {
		return cts;
	}
	public void setCts(long cts) {
		this.cts = cts;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
