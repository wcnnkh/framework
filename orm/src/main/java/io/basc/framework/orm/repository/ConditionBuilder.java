package io.basc.framework.orm.repository;

import io.basc.framework.data.repository.ConditionSymbol;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

public final class ConditionBuilder {
	private ConditionSymbol symbol;
	private Parameter parameter;

	public Condition build() {
		return new Condition(this.symbol, this.parameter);
	}

	public ConditionBuilder endsWith() {
		return with(ConditionSymbol.ENDS_WITH);
	}

	public ConditionBuilder equal() {
		return with(ConditionSymbol.EQU);
	}

	public ConditionBuilder equalOrGreaterThan() {
		return with(ConditionSymbol.GEQ);
	}

	public ConditionBuilder equalOrLessThan() {
		return with(ConditionSymbol.LEQ);
	}

	public ConditionBuilder greaterThan() {
		return with(ConditionSymbol.GTR);
	}

	public ConditionBuilder in() {
		return with(ConditionSymbol.IN);
	}

	public ConditionBuilder lessThan() {
		return with(ConditionSymbol.LSS);
	}

	public ConditionBuilder like() {
		return with(ConditionSymbol.LIKE);
	}

	public ConditionBuilder name(String name) {
		if (parameter == null) {
			this.parameter = new Parameter(name);
		} else {
			this.parameter = this.parameter.rename(name);
		}
		return this;
	}

	public ConditionBuilder not() {
		return with(ConditionSymbol.NEQ);
	}

	public ConditionBuilder parameter(Parameter parameter) {
		this.parameter = parameter;
		return this;
	}

	public ConditionBuilder parameter(String name, Object value) {
		return parameter(new Parameter(name, value));
	}

	public ConditionBuilder search() {
		return with(ConditionSymbol.SEARCH);
	}

	public ConditionBuilder startsWith() {
		return with(ConditionSymbol.STARTS_WITH);
	}

	public ConditionBuilder value(Object value) {
		Assert.requiredArgument(parameter != null && StringUtils.isNotEmpty(parameter.getName()), "parameter");
		this.parameter.setValue(value);
		return this;
	}

	public ConditionBuilder with(ConditionSymbol symbol) {
		this.symbol = symbol;
		return this;
	}
}
