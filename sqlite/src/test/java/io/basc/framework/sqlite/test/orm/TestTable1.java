package io.basc.framework.sqlite.test.orm;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.orm.annotation.PrimaryKey;
import io.basc.framework.sql.orm.annotation.Table;

@Table
public class TestTable1 {
	@PrimaryKey
	private int id;
	private String key;
	private int value;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return ReflectionUtils.toString(this);
	}
}
