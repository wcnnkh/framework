package io.basc.framework.sql;

import java.io.Serializable;

import io.basc.framework.util.ArrayUtils;

public class SqlExpression implements Sql, Serializable {
	private static final long serialVersionUID = 1L;
	private final String name;
	private final Sql left;
	private final CharSequence operator;
	private final Sql right;

	public SqlExpression(String name, Sql left, CharSequence operator, Sql right) {
		this.name = name;
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	public String getName() {
		return name;
	}

	public Sql getLeft() {
		return left;
	}

	public CharSequence getOperator() {
		return operator;
	}

	public Sql getRight() {
		return right;
	}

	@Override
	public String toString() {
		return SqlUtils.toString(getSql(), getParams());
	}

	@Override
	public String getSql() {
		return left.getSql() + operator + right.getSql();
	}

	@Override
	public Object[] getParams() {
		return ArrayUtils.merge(left.getParams(), right.getParams());
	}
}
