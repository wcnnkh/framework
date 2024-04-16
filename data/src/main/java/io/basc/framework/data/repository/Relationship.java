package io.basc.framework.data.repository;

import java.util.List;

import lombok.Data;

@Data
public class Relationship<T> {
	private final List<Condition> conditions;

	protected Relationship(List<Condition> conditionns) {
		this.conditions = conditionns;
	}

	public ConditionsBuilder<T> relationship(RelationshipSymbol relationshipSymbol) {
		return new ConditionsBuilder<>(conditions, relationshipSymbol);
	}

	public ConditionsBuilder<T> and() {
		return relationship(RelationshipSymbol.AND);
	}

	public ConditionsBuilder<T> or() {
		return relationship(RelationshipSymbol.OR);
	}
}
