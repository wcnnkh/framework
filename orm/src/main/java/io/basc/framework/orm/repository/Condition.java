package io.basc.framework.orm.repository;

import java.io.Serializable;

import io.basc.framework.env.BascObject;

/**
 * 条件
 * 
 * @author wcnnkh
 *
 */
public class Condition extends BascObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String condition;
	private final RepositoryColumn column;

	/**
	 * @param condition = > < any with startWith endWith ...
	 * @param column
	 */
	public Condition(String condition, RepositoryColumn column) {
		this.condition = condition;
		this.column = column;
	}

	public String getCondition() {
		return condition;
	}

	public RepositoryColumn getColumn() {
		return column;
	}

}
