package io.basc.framework.orm.repository;

import io.basc.framework.mapper.Parameter;
import io.basc.framework.util.Assert;

public final class ConditionBuilder {
	private final ConditionKeywords conditionKeywords;
	private String condition;
	private Parameter parameter;

	public ConditionBuilder(ConditionKeywords conditionKeywords) {
		this.conditionKeywords = conditionKeywords;
	}

	public Condition build() {
		return new Condition(this.condition, this.parameter);
	}

	public ConditionBuilder with(String condition) {
		this.condition = condition;
		return this;
	}

	public ConditionBuilder name(String name) {
		if (parameter == null) {
			this.parameter = new Parameter(name);
		} else {
			this.parameter = this.parameter.rename(name);
		}
		return this;
	}

	public ConditionBuilder parameter(Parameter parameter) {
		this.parameter = parameter;
		return this;
	}

	public ConditionBuilder parameter(String name, Object value) {
		return parameter(new Parameter(name, value));
	}

	public ConditionBuilder value(Object value) {
		Assert.requiredArgument(parameter != null && !parameter.isInvalid(), "parameter");
		this.parameter.setValue(value);
		return this;
	}

	public ConditionBuilder equal() {
		return with(conditionKeywords.getEqualKeywords().getFirst());
	}

	public ConditionBuilder endWith() {
		return with(conditionKeywords.getEndWithKeywords().getFirst());
	}

	public ConditionBuilder equalOrGreaterThan() {
		return with(conditionKeywords.getEqualOrGreaterThanKeywords().getFirst());
	}

	public ConditionBuilder equalOrLessThan() {
		return with(conditionKeywords.getEqualOrLessThanKeywords().getFirst());
	}

	public ConditionBuilder greaterThan() {
		return with(conditionKeywords.getGreaterThanKeywords().getFirst());
	}

	public ConditionBuilder in() {
		return with(conditionKeywords.getInKeywords().getFirst());
	}

	public ConditionBuilder lessThan() {
		return with(conditionKeywords.getLessThanKeywords().getFirst());
	}

	public ConditionBuilder like() {
		return with(conditionKeywords.getLikeKeywords().getFirst());
	}

	public ConditionBuilder not() {
		return with(conditionKeywords.getNotEqualKeywords().getFirst());
	}

	public ConditionBuilder search() {
		return with(conditionKeywords.getSearchKeywords().getFirst());
	}

	public ConditionBuilder startWith() {
		return with(conditionKeywords.getStartWithKeywords().getFirst());
	}
}
