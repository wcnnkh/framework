package io.basc.framework.orm.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.basc.framework.mapper.Parameter;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Pair;
import io.basc.framework.util.Processor;

public final class ConditionsBuilder {
	private final RelationshipKeywords relationshipKeywords;
	private final ConditionKeywords conditionKeywords;
	private final Condition condition;
	private List<WithCondition> withs = new ArrayList<WithCondition>();

	public ConditionsBuilder(RelationshipKeywords relationshipKeywords, ConditionKeywords conditionKeywords) {
		this(relationshipKeywords, conditionKeywords,
				new Condition(conditionKeywords.getEqualKeywords().getFirst(), Parameter.INVALID));
	}

	public ConditionsBuilder(RelationshipKeywords relationshipKeywords, ConditionKeywords conditionKeywords,
			Condition condition) {
		Assert.requiredArgument(relationshipKeywords != null, "relationshipKeywords");
		Assert.requiredArgument(conditionKeywords != null, "conditionKeywords");
		Assert.requiredArgument(condition != null, "condition");
		this.relationshipKeywords = relationshipKeywords;
		this.conditionKeywords = conditionKeywords;
		this.condition = condition;
	}

	public <E extends Throwable> ConditionsBuilder(RelationshipKeywords relationshipKeywords,
			ConditionKeywords conditionKeywords,
			Processor<? super ConditionBuilder, ? extends Condition, ? extends E> conditionBuilder) throws E {
		Assert.requiredArgument(relationshipKeywords != null, "relationshipKeywords");
		Assert.requiredArgument(conditionKeywords != null, "conditionKeywords");
		Assert.requiredArgument(conditionBuilder != null, "conditionBuilder");
		this.relationshipKeywords = relationshipKeywords;
		this.conditionKeywords = conditionKeywords;
		this.condition = conditionBuilder.process(new ConditionBuilder(conditionKeywords));
	}

	public ConditionsBuilder newBuilder() {
		return new ConditionsBuilder(relationshipKeywords, conditionKeywords);
	}

	public ConditionsBuilder newBuilder(Condition condition) {
		return new ConditionsBuilder(relationshipKeywords, conditionKeywords, condition);
	}

	public <E extends Throwable> ConditionsBuilder newBuilder(
			Processor<ConditionBuilder, Condition, E> conditionBuilder) throws E {
		return newBuilder(conditionBuilder.process(new ConditionBuilder(conditionKeywords)));
	}

	public ConditionsBuilder with(WithCondition condition) {
		this.withs.add(condition);
		return this;
	}

	public ConditionsBuilder withConditions(String relationship, Conditions conditions) {
		return with(new WithCondition(relationship, conditions));
	}

	public <E extends Throwable> ConditionsBuilder withConditions(String relationship,
			Processor<ConditionsBuilder, Conditions, E> conditionsBuilder) throws E {
		return withConditions(relationship,
				conditionsBuilder.process(new ConditionsBuilder(relationshipKeywords, conditionKeywords)));
	}

	public ConditionsBuilder and(Conditions conditions) {
		return withConditions(relationshipKeywords.getAndKeywords().getFirst(), conditions);
	}

	public ConditionsBuilder or(Conditions conditions) {
		return withConditions(relationshipKeywords.getOrKeywords().getFirst(), conditions);
	}

	public <E extends Throwable> ConditionsBuilder andConditions(Processor<ConditionsBuilder, Conditions, E> processor)
			throws E {
		return and(processor.process(new ConditionsBuilder(relationshipKeywords, conditionKeywords)));
	}

	public <E extends Throwable> ConditionsBuilder orConditions(Processor<ConditionsBuilder, Conditions, E> processor)
			throws E {
		return or(processor.process(new ConditionsBuilder(relationshipKeywords, conditionKeywords)));
	}

	public ConditionsBuilder withCondition(String relationship, Condition conditions) {
		return withConditions(relationship, new Conditions(conditions, null));
	}

	public <E extends Throwable> ConditionsBuilder withCondition(String relationship,
			Processor<ConditionBuilder, Condition, E> conditionBuilder) throws E {
		return withCondition(relationship, conditionBuilder.process(new ConditionBuilder(this.conditionKeywords)));
	}

	public ConditionsBuilder with(String relationship, String name, String condition, Object value) {
		return withCondition(relationship, new Condition(name, new Parameter(name, value)));
	}

	public ConditionsBuilder and(Condition condition) {
		return withCondition(relationshipKeywords.getAndKeywords().getFirst(), condition);
	}

	public <E extends Throwable> ConditionsBuilder and(Processor<ConditionBuilder, Condition, E> conditionBuilder)
			throws E {
		return and(conditionBuilder.process(new ConditionBuilder(conditionKeywords)));
	}

	public ConditionsBuilder or(Condition condition) {
		return withCondition(relationshipKeywords.getOrKeywords().getFirst(), condition);
	}

	public <E extends Throwable> ConditionsBuilder or(Processor<ConditionBuilder, Condition, E> conditionBuilder)
			throws E {
		return or(conditionBuilder.process(new ConditionBuilder(this.conditionKeywords)));
	}

	public ConditionsBuilder and(String name, String condition, Object value) {
		return and(new Condition(name, new Parameter(name, value)));
	}

	public ConditionsBuilder or(String name, String condition, Object value) {
		return or(new Condition(name, new Parameter(name, value)));
	}

	public Conditions build() {
		return new Conditions(condition, withs);
	}

	public static Conditions build(Elements<Pair<String, Condition>> conditions) {
		return build(conditions.iterator());
	}

	public static Conditions build(Iterator<Pair<String, Condition>> iterator) {
		if (iterator == null || !iterator.hasNext()) {
			return null;
		}
		Pair<String, Condition> pair = iterator.next();
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
