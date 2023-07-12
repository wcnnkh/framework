package io.basc.framework.orm;

import java.util.List;
import java.util.function.Function;

import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.RelationshipSymbol;
import lombok.Data;

@Data
public class ConditionsBuilder<T> {
	private final List<Condition> conditions;
	private final RelationshipSymbol relationshipSymbol;

	public <V> Relationship<T> eq(Function<? super T, ? extends V> function, V value) {
		return new Relationship<>(conditions);
	}
}
