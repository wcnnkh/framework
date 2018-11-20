package shuchaowen.cms.pojo;

import shuchaowen.core.db.annoation.PrimaryKey;
import shuchaowen.core.db.annoation.Table;

@Table
public class ChannelModelAttr{
	@PrimaryKey
	private int id;
	@PrimaryKey
	private String attr;
	private String attrName;
	private int weight;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public String getAttrName() {
		return attrName;
	}
	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
