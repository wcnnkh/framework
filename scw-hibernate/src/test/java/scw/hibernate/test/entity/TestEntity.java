package scw.hibernate.test.entity;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
public class TestEntity {
	@NotNull
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
