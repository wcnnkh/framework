package io.basc.framework.orm;

import java.util.List;

import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.RelationshipSymbol;
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
