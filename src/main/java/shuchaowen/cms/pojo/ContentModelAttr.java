package shuchaowen.cms.pojo;

import java.io.Serializable;

import shuchaowen.core.db.annoation.Column;
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
	@Column(length=0, type="text", nullAble=true)
	private String defaultValue;
	private int attrType;//类型
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
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public int getAttrType() {
		return attrType;
	}
	public void setAttrType(int attrType) {
		this.attrType = attrType;
	}
}
