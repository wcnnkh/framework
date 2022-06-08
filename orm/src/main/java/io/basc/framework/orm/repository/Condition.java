package io.basc.framework.orm.repository;

import java.io.Serializable;

import io.basc.framework.env.BascObject;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.util.StringUtils;

/**
 * 条件
 * 
 * @author wcnnkh
 *
 */
public class Condition extends BascObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String condition;
	private final Parameter parameter;

	/**
	 * @param condition = > < any with startWith endWith ...
	 * @param parameter
	 */
	public Condition(String condition, Parameter parameter) {
		this.condition = condition;
		this.parameter = parameter;
	}

	public String getCondition() {
		return condition;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public boolean isInvalid() {
		return StringUtils.isEmpty(condition) || parameter.isInvalid();
	}
}
