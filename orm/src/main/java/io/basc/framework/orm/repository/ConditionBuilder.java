package io.basc.framework.orm.repository;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;

public final class ConditionBuilder {
	private final ConditionKeywords conditionKeywords;
	private String condition;
	private String name;
	private Object value;
	private TypeDescriptor valueTypeDescriptor;

	public ConditionBuilder(ConditionKeywords conditionKeywords) {
		this.conditionKeywords = conditionKeywords;
	}

	public Condition build() {
		return new Condition(this.condition, new RepositoryColumn(this.name, this.value, this.valueTypeDescriptor));
	}

	public ConditionBuilder with(String condition) {
		this.condition = condition;
		return this;
	}

	public ConditionBuilder value(Object value, @Nullable TypeDescriptor valueTypeDescriptor) {
		this.value = value;
		this.valueTypeDescriptor = valueTypeDescriptor;
		return this;
	}

	public ConditionBuilder value(Object value) {
		return value(value, null);
	}

	public ConditionBuilder name(String name) {
		this.name = name;
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
