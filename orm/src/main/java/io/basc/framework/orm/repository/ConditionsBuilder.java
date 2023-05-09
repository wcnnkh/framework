package io.basc.framework.orm.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.basc.framework.data.repository.ConditionSymbol;
import io.basc.framework.data.repository.RelationshipSymbol;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Pair;
import io.basc.framework.util.Processor;

public final class ConditionsBuilder {
	private final Condition condition;
	private List<WithCondition> withs = new ArrayList<WithCondition>();

	public ConditionsBuilder() {
		this(new Condition(ConditionSymbol.EQU, Parameter.INVALID));
	}

	public ConditionsBuilder(Condition condition) {
		Assert.requiredArgument(condition != null, "condition");
		this.condition = condition;
	}

	public <E extends Throwable> ConditionsBuilder(
			Processor<? super ConditionBuilder, ? extends Condition, ? extends E> conditionBuilder) throws E {
		Assert.requiredArgument(conditionBuilder != null, "conditionBuilder");
		this.condition = conditionBuilder.process(new ConditionBuilder());
	}

	public ConditionsBuilder newBuilder() {
		return new ConditionsBuilder();
	}

	public ConditionsBuilder newBuilder(Condition condition) {
		return new ConditionsBuilder(condition);
	}

	public <E extends Throwable> ConditionsBuilder newBuilder(
			Processor<ConditionBuilder, Condition, E> conditionBuilder) throws E {
		return newBuilder(conditionBuilder.process(new ConditionBuilder()));
	}

	public ConditionsBuilder with(WithCondition condition) {
		this.withs.add(condition);
		return this;
	}

	public ConditionsBuilder withConditions(RelationshipSymbol relationship, Conditions conditions) {
		return with(new WithCondition(relationship, conditions));
	}

	public <E extends Throwable> ConditionsBuilder withConditions(RelationshipSymbol relationship,
			Processor<ConditionsBuilder, Conditions, E> conditionsBuilder) throws E {
		return withConditions(relationship, conditionsBuilder.process(new ConditionsBuilder()));
	}

	public ConditionsBuilder and(Conditions conditions) {
		return withConditions(RelationshipSymbol.AND, conditions);
	}

	public ConditionsBuilder or(Conditions conditions) {
		return withConditions(RelationshipSymbol.OR, conditions);
	}

	public <E extends Throwable> ConditionsBuilder andConditions(Processor<ConditionsBuilder, Conditions, E> processor)
			throws E {
		return and(processor.process(new ConditionsBuilder()));
	}

	public <E extends Throwable> ConditionsBuilder orConditions(Processor<ConditionsBuilder, Conditions, E> processor)
			throws E {
		return or(processor.process(new ConditionsBuilder()));
	}

	public ConditionsBuilder withCondition(RelationshipSymbol relationship, Condition conditions) {
		return withConditions(relationship, new Conditions(conditions, null));
	}

	public <E extends Throwable> ConditionsBuilder withCondition(RelationshipSymbol relationship,
			Processor<ConditionBuilder, Condition, E> conditionBuilder) throws E {
		return withCondition(relationship, conditionBuilder.process(new ConditionBuilder()));
	}

	public ConditionsBuilder with(RelationshipSymbol relationship, String name, ConditionSymbol condition,
			Object value) {
		return withCondition(relationship, new Condition(condition, new Parameter(name, value)));
	}

	public ConditionsBuilder and(Condition condition) {
		return withCondition(RelationshipSymbol.AND, condition);
	}

	public <E extends Throwable> ConditionsBuilder and(Processor<ConditionBuilder, Condition, E> conditionBuilder)
			throws E {
		return and(conditionBuilder.process(new ConditionBuilder()));
	}

	public ConditionsBuilder or(Condition condition) {
		return withCondition(RelationshipSymbol.OR, condition);
	}

	public <E extends Throwable> ConditionsBuilder or(Processor<ConditionBuilder, Condition, E> conditionBuilder)
			throws E {
		return or(conditionBuilder.process(new ConditionBuilder()));
	}

	public ConditionsBuilder and(String name, ConditionSymbol condition, Object value) {
		return and(new Condition(condition, new Parameter(name, value)));
	}

	public ConditionsBuilder or(String name, ConditionSymbol condition, Object value) {
		return or(new Condition(condition, new Parameter(name, value)));
	}

	public Conditions build() {
		return new Conditions(condition, withs);
	}

	public static Conditions build(Elements<Pair<RelationshipSymbol, Condition>> conditions) {
		return build(conditions.iterator());
	}

	public static Conditions build(Iterator<Pair<RelationshipSymbol, Condition>> iterator) {
		if (iterator == null || !iterator.hasNext()) {
			return null;
		}
		Pair<RelationshipSymbol, Condition> pair = iterator.next();
		Condition root = pair.getValue();
		List<WithCondition> list = new ArrayList<WithCondition>();
		while (iterator.hasNext()) {
			pair = iterator.next();
			if (pair == null) {
				continue;
			}

			if (pair.getValue() == null) {
				continue;
			}

			list.add(new WithCondition(pair.getKey(), new Conditions(pair.getValue(), null)));
		}
		return new Conditions(root, list);
	}
}
