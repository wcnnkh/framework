package io.basc.framework.sqlite.test.orm;

import io.basc.framework.orm.annotation.PrimaryKey;
import io.basc.framework.sql.orm.annotation.AutoIncrement;

public class AutoIncrementTestBean {
	@AutoIncrement
	@PrimaryKey
	private int id;
	
	private String value;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
