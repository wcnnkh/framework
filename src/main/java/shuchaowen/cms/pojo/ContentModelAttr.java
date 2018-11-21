package shuchaowen.cms.pojo;

import java.io.Serializable;

import shuchaowen.core.db.annoation.PrimaryKey;
import shuchaowen.core.db.annoation.Table;

@Table
public class ContentModelAttr implements Serializable{
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private long modelId;
	@PrimaryKey
	private String attr;
	private String attrName;
	private int weight;
	
	public long getModelId() {
		return modelId;
	}
	public void setModelId(long modelId) {
		this.modelId = modelId;
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
