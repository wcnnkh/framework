package scw.orm;

import java.io.Serializable;

public class SharedProperty implements PropertyDescribe, Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private boolean primaryKey;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}
}
