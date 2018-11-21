package shuchaowen.cms.pojo;

import java.io.Serializable;

import shuchaowen.core.db.annoation.PrimaryKey;
import shuchaowen.core.db.annoation.Table;

@Table
public class ContentKey implements Serializable{
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private long id;
	@PrimaryKey
	private String key;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
}
