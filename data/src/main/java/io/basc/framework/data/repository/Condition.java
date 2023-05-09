package io.basc.framework.data.repository;

import java.io.Serializable;

import io.basc.framework.mapper.Parameter;
import lombok.Data;

/**
 * 条件
 * 
 * @author wcnnkh
 *
 */
@Data
public class Condition implements Serializable {
	private static final long serialVersionUID = 1L;
	private final ConditionSymbol symbol;
	private final Parameter parameter;

	public Condition(ConditionSymbol symbol, Parameter parameter) {
		this.symbol = symbol;
		this.parameter = parameter;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public boolean isInvalid() {
		return symbol == null || !parameter.isPresent();
	}
}
