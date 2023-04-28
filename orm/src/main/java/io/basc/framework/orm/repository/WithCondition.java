package io.basc.framework.orm.repository;

import java.io.Serializable;

import lombok.Data;

@Data
public class WithCondition implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String with;
	private final Conditions condition;

	public WithCondition(String with, Conditions condition) {
		this.with = with;
		this.condition = condition;
	}

	public String getWith() {
		return with;
	}

	public Conditions getCondition() {
		return condition;
	}
}
