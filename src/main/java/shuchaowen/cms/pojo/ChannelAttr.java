package shuchaowen.cms.pojo;

import java.io.Serializable;

import shuchaowen.core.db.annoation.Column;
import shuchaowen.core.db.annoation.PrimaryKey;

public class ChannelAttr implements Serializable{
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private int channelId;
	@PrimaryKey
	private String attr;
	@Column(length=0, type="text", nullAble=true)
	private String value;
	private int modelId;
	public int getChannelId() {
		return channelId;
	}
	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}
	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getModelId() {
		return modelId;
	}
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}
}
