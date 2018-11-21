package shuchaowen.cms.pojo;

import java.io.Serializable;

import shuchaowen.core.db.annoation.PrimaryKey;
import shuchaowen.core.db.annoation.Table;

@Table
public class ContentModel implements Serializable{
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private long id;
	private String name;
	private int weight;
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
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
