package scw.mvc.logger.db;

import java.io.Serializable;

import scw.orm.annotation.PrimaryKey;

public class LogAttributeTable implements Serializable{
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private String logId;
	@PrimaryKey
	private String name;
	private String value;
	
	public String getLogId() {
		return logId;
	}
	public void setLogId(String logId) {
		this.logId = logId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
