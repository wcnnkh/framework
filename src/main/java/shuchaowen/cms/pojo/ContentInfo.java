package shuchaowen.cms.pojo;

import java.io.Serializable;

import shuchaowen.core.db.annoation.Column;
import shuchaowen.core.db.annoation.PrimaryKey;
import shuchaowen.core.db.annoation.Table;

@Table
public class ContentInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private long id;
	@Column(length=0, type="text")
	private String content;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
