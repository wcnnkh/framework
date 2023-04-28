package io.basc.framework.orm;

import java.io.Serializable;

import lombok.Data;

@Data
public class ForeignKey implements Serializable {
	private static final long serialVersionUID = 1L;
	private Class<?> entityClass;
	private String name;

	public ForeignKey() {
	}

	public ForeignKey(Class<?> entityClass, String name) {
		this.entityClass = entityClass;
		this.name = name;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
