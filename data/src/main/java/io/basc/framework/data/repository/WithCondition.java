package io.basc.framework.data.repository;

import java.io.Serializable;

import lombok.Data;

@Data
public class WithCondition implements Serializable {
	private static final long serialVersionUID = 1L;
	private final RelationshipSymbol relationship;
	private final Conditions condition;

	public WithCondition(RelationshipSymbol relationship, Conditions condition) {
		this.relationship = relationship;
		this.condition = condition;
	}
}
