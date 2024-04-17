package io.basc.framework.lucene.test;

import io.basc.framework.orm.stereotype.PrimaryKey;
import lombok.Data;

@Data
public class TestBean {
	@PrimaryKey
	private String name;
	private String value;

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
